/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
** Copyright (C) 2013 Peter Droste, Omix Visualization GmbH & Co. KG. All rights reserved.
**
** This file is part of Qt Jambi.
**
** ** $BEGIN_LICENSE$
**
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
**
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
**
** $END_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#include "abstractmetabuilder.h"
#include "reporthandler.h"

#include "ast.h"
#include "binder.h"
#include "control.h"
#include "default_visitor.h"
#include "dumptree.h"
#include "lexer.h"
#include "parser.h"
#include "tokens.h"

#include <QDebug>
#include <QDir>
#include <QFile>
#include <QFileInfo>
#include <QTextCodec>
#include <QTextStream>
#include <QVariant>

static QString strip_template_args(const QString &name) {
    int pos = name.indexOf('<');
    return pos < 0 ? name : name.left(pos);
}

static QHash<QString, QString> *operator_names;

/**
 * Checks if operator is legit and returns returns
 * corresponding operator with prefix.
 *
 * For example for "+" return "operator_add"
 */
QString rename_operator(const QString &oper) {
    QString op = oper.trimmed();
    if (!operator_names) {
        operator_names = new QHash<QString, QString>;

        operator_names->insert("+", "add");
        operator_names->insert("-", "subtract");
        operator_names->insert("*", "multiply");
        operator_names->insert("/", "divide");
        operator_names->insert("%", "modulo");
        operator_names->insert("&", "and");
        operator_names->insert("|", "or");
        operator_names->insert("^", "xor");
        operator_names->insert("~", "negate");
        operator_names->insert("<<", "shift_left");
        operator_names->insert(">>", "shift_right");

        // assigments
        operator_names->insert("=", "assign");
        operator_names->insert("+=", "add_assign");
        operator_names->insert("-=", "subtract_assign");
        operator_names->insert("*=", "multiply_assign");
        operator_names->insert("/=", "divide_assign");
        operator_names->insert("%=", "modulo_assign");
        operator_names->insert("&=", "and_assign");
        operator_names->insert("|=", "or_assign");
        operator_names->insert("^=", "xor_assign");
        operator_names->insert("<<=", "shift_left_assign");
        operator_names->insert(">>=", "shift_right_assign");

        // Logical
        operator_names->insert("&&", "logical_and");
        operator_names->insert("||", "logical_or");
        operator_names->insert("!", "not");

        // incr/decr
        operator_names->insert("++", "increment");
        operator_names->insert("--", "decrement");

        // compare
        operator_names->insert("<", "less");
        operator_names->insert(">", "greater");
        operator_names->insert("<=", "less_or_equal");
        operator_names->insert(">=", "greater_or_equal");
        operator_names->insert("!=", "not_equal");
        operator_names->insert("==", "equal");

        // other
        operator_names->insert("[]", "subscript");
        operator_names->insert("->", "pointer");
        operator_names->insert("()", "call");
    }

    if (!operator_names->contains(op)) {
        TypeDatabase *tb = TypeDatabase::instance();

        TypeParser::Info typeInfo = TypeParser::parse(op);
        QString cast_to_name = typeInfo.qualified_name.join("::");
        TypeEntry *te = tb->findType(cast_to_name);
        if ((te && te->codeGeneration() == TypeEntry::GenerateNothing)
                || tb->isClassRejected(cast_to_name)) {
            return QString();
        } else if (te) {
            return "operator_cast_" + typeInfo.qualified_name.join("_");
        } else {
            ReportHandler::warning(QString("unknown operator '%1'").arg(op));
            return "operator " + op;
        }
    }

    return "operator_" + operator_names->value(op);
}

AbstractMetaBuilder::AbstractMetaBuilder()
        : m_current_class(0) {
}

/**
 * This function is here, simply to print out warnings about
 * modifications not having the proper signature...
 */
void AbstractMetaBuilder::checkFunctionModifications() {
    TypeDatabase *types = TypeDatabase::instance();
    SingleTypeEntryHash entryHash = types->entries();
    QList<TypeEntry *> entries = entryHash.values();
    foreach(TypeEntry *entry, entries) {
        if (entry == 0)
            continue;
        if (!entry->isComplex() || entry->codeGeneration() == TypeEntry::GenerateNothing)
            continue;

        ComplexTypeEntry *centry = static_cast<ComplexTypeEntry *>(entry);
        FunctionModificationList modifications = centry->functionModifications();

        foreach(FunctionModification modification, modifications) {
            QString signature = modification.signature;

            QString name = signature.trimmed();
            name = name.mid(0, signature.indexOf("("));

            AbstractMetaClass *clazz = m_meta_classes.findClass(centry->qualifiedCppName());
            if (clazz == 0)
                continue;

            AbstractMetaFunctionList functions = clazz->functions();
            bool found = false;
            QStringList possibleSignatures;
            foreach(AbstractMetaFunction *function, functions) {
                if (function->minimalSignature() == signature && function->implementingClass() == clazz) {
                    found = true;
                    break;
                }

                if (function->originalName() == name)
                    possibleSignatures.append(function->minimalSignature() + " in " + function->implementingClass()->name());
            }

            if (!found) {
                QString warning
                = QString("signature '%1' for function modification in '%2' not found. Possible candidates: %3")
                  .arg(signature)
                  .arg(clazz->qualifiedCppName())
                  .arg(possibleSignatures.join(", "));

                ReportHandler::warning(warning);
            }
        }
    }
}

AbstractMetaClass *AbstractMetaBuilder::argumentToClass(ArgumentModelItem argument, const QString &contextString) {
    AbstractMetaClass *returned = 0;
    bool ok = false;
    AbstractMetaType *type = translateType(argument->type(), &ok, contextString);
    if (ok && type != 0 && type->typeEntry() != 0 && type->typeEntry()->isComplex()) {
        const TypeEntry *entry = type->typeEntry();
        returned = m_meta_classes.findClass(entry->name());
    }
    delete type;
    return returned;
}

/**
 * Checks the argument of a hash function and flags the type if it is a complex type
 */
void AbstractMetaBuilder::registerHashFunction(FunctionModelItem function_item) {
    ArgumentList arguments = function_item->arguments();
    if (arguments.size() == 1) {
        if (AbstractMetaClass *cls = argumentToClass(arguments.at(0), "AbstractMetaBuilder::registerHashFunction")) {
            QFileInfo info(function_item->fileName());
            cls->typeEntry()->addExtraInclude(Include(Include::IncludePath, info.fileName()));

            cls->setHasHashFunction(true);
        }
    }
}

/**
 * Check if a class has a debug stream operator that can be used as toString
 */
void AbstractMetaBuilder::registerToStringCapability(FunctionModelItem function_item) {
    ArgumentList arguments = function_item->arguments();
    if (arguments.size() == 2) {
        if (arguments.at(0)->type().toString() == "QDebug") {
            ArgumentModelItem arg = arguments.at(1);
            if (AbstractMetaClass *cls = argumentToClass(arg, "AbstractMetaBuilder::registerToStringCapability")) {
                if (arg->type().indirections().size() < 2) {
                    cls->setToStringCapability(function_item);
                }
            }
        }
    }
}

void AbstractMetaBuilder::traverseCompareOperator(FunctionModelItem item) {
    ArgumentList arguments = item->arguments();
    if (arguments.size() == 2 && item->accessPolicy() == CodeModel::Public) {
        AbstractMetaClass *comparer_class = argumentToClass(arguments.at(0), "AbstractMetaBuilder::traverseCompareOperator comparer_class");
        AbstractMetaClass *compared_class = argumentToClass(arguments.at(1), "AbstractMetaBuilder::traverseCompareOperator compared_class");
        if (comparer_class != 0 && compared_class != 0) {
            AbstractMetaClass *old_current_class = m_current_class;
            m_current_class = comparer_class;

            AbstractMetaFunction *meta_function = traverseFunction(item);
            if (meta_function != 0 && !meta_function->isInvalid()) {
                // Strip away first argument, since that is the containing object
                AbstractMetaArgumentList arguments = meta_function->arguments();
                arguments.pop_front();
                meta_function->setArguments(arguments);

                meta_function->setFunctionType(AbstractMetaFunction::GlobalScopeFunction);

                meta_function->setOriginalAttributes(meta_function->attributes());
                setupFunctionDefaults(meta_function, comparer_class);

                comparer_class->addFunction(meta_function);
            } else if (meta_function != 0) {
                delete meta_function;
            }

            m_current_class = old_current_class;
        }
    }
}

void AbstractMetaBuilder::traverseStreamOperator(FunctionModelItem item) {
    ArgumentList arguments = item->arguments();
    if (arguments.size() == 2 && item->accessPolicy() == CodeModel::Public) {
        AbstractMetaClass *streamClass = argumentToClass(arguments.at(0), "AbstractMetaBuilder::traverseStreamOperator streamClass");
        AbstractMetaClass *streamedClass = argumentToClass(arguments.at(1), "AbstractMetaBuilder::traverseStreamOperator streamedClass");

        if (streamClass != 0 && streamedClass != 0
                && (streamClass->name() == "QDataStream" || streamClass->name() == "QTextStream")) {
            AbstractMetaClass *old_current_class = m_current_class;
            m_current_class = streamedClass;
            AbstractMetaFunction *streamFunction = traverseFunction(item);

            if (streamFunction != 0 && !streamFunction->isInvalid()) {
                QString name = item->name();
                streamFunction->setFunctionType(AbstractMetaFunction::GlobalScopeFunction);

                if (name.endsWith("<<"))
                    streamFunction->setName("writeTo");
                else
                    streamFunction->setName("readFrom");

                // Strip away last argument, since that is the containing object
                AbstractMetaArgumentList arguments = streamFunction->arguments();
                arguments.pop_back();
                streamFunction->setArguments(arguments);

                *streamFunction += AbstractMetaAttributes::Final;
                *streamFunction += AbstractMetaAttributes::Public;
                streamFunction->setOriginalAttributes(streamFunction->attributes());

                streamFunction->setType(0);

                setupFunctionDefaults(streamFunction, streamedClass);

                streamedClass->addFunction(streamFunction);
                streamedClass->typeEntry()->addExtraInclude(streamClass->typeEntry()->include());

                m_current_class = old_current_class;
            }
        }
    }
}

void AbstractMetaBuilder::fixQObjectForScope(TypeDatabase *types,
        NamespaceModelItem scope) {
    foreach(ClassModelItem item, scope->classes()) {
        QString qualified_name = item->qualifiedName().join("::");
        TypeEntry *entry = types->findType(qualified_name);
        if (entry) {
            if (isQObject(qualified_name) && entry->isComplex()) {
                ((ComplexTypeEntry *) entry)->setQObject(true);
            }
        }
    }

    foreach(NamespaceModelItem item, scope->namespaceMap().values()) {
        if (scope != item)
            fixQObjectForScope(types, item);
    }
}

static bool class_less_than(AbstractMetaClass *a, AbstractMetaClass *b) {
    return a->name() < b->name();
}


void AbstractMetaBuilder::sortLists() {
    qSort(m_meta_classes.begin(), m_meta_classes.end(), class_less_than);
    foreach(AbstractMetaClass *cls, m_meta_classes) {
        cls->sortFunctions();
    }
}

bool AbstractMetaBuilder::build() {
    Q_ASSERT(!m_file_name.isEmpty());
    ReportHandler::setContext("Parser");

    QFile file(m_file_name);

    if (!file.open(QFile::ReadOnly))
        return false;

    QTextStream stream(&file);
    stream.setCodec(QTextCodec::codecForName("UTF-8"));
    QByteArray contents = stream.readAll().toUtf8();
    file.close();

    Control control;
    Parser p(&control);
    pool __pool;

    TranslationUnitAST *ast = p.parse(contents, contents.size(), &__pool);

    CodeModel model;
    Binder binder(&model, p.location());
    m_dom = binder.run(ast);

    ReportHandler::setContext("MetaJavaBuilder");

    pushScope(model_dynamic_cast<ScopeModelItem>(m_dom));

    QHash<QString, ClassModelItem> typeMap = m_dom->classMap();

    // fix up QObject's in the type system..
    TypeDatabase *types = TypeDatabase::instance();
    fixQObjectForScope(types, model_dynamic_cast<NamespaceModelItem>(m_dom));

    // Start the generation...
    foreach(ClassModelItem item, typeMap.values()) {
        AbstractMetaClass *cls = traverseClass(item);
        addAbstractMetaClass(cls);
    }

    QHash<QString, NamespaceModelItem> namespaceMap = m_dom->namespaceMap();
    foreach(NamespaceModelItem item, namespaceMap.values()) {
        AbstractMetaClass *meta_class = traverseNamespace(item);
        if (meta_class)
            m_meta_classes << meta_class;
    }

    // Some trickery to support global-namespace enums...
    QHash<QString, EnumModelItem> enumMap = m_dom->enumMap();
    m_current_class = 0;
    foreach(EnumModelItem item, enumMap) {
        AbstractMetaEnum *meta_enum = traverseEnum(item, 0, QSet<QString>());

        if (meta_enum) {
            QString package = meta_enum->typeEntry()->javaPackage();
            QString globalName = TypeDatabase::globalNamespaceClassName(meta_enum->typeEntry());

            AbstractMetaClass *global = m_meta_classes.findClass(package + "." + globalName);
            if (!global) {
                ComplexTypeEntry *gte = new ObjectTypeEntry(globalName);
                gte->setTargetLangPackage(meta_enum->typeEntry()->javaPackage());
                gte->setCodeGeneration(meta_enum->typeEntry()->codeGeneration());
                global = createMetaClass();
                global->setTypeEntry(gte);
                *global += AbstractMetaAttributes::Final;
                *global += AbstractMetaAttributes::Public;
                *global += AbstractMetaAttributes::Fake;

                m_meta_classes << global;
            }

            global->addEnum(meta_enum);
            meta_enum->setEnclosingClass(global);
            meta_enum->typeEntry()->setQualifier(globalName);
        }
    }

    // Go through all typedefs to see if we have defined any
    // specific typedefs to be used as classes.
    TypeAliasList typeAliases = m_dom->typeAliases();
    foreach(TypeAliasModelItem typeAlias, typeAliases) {
        AbstractMetaClass *cls = traverseTypeAlias(typeAlias);
        addAbstractMetaClass(cls);
    }

    foreach(AbstractMetaClass *cls, m_meta_classes) {
        if (!cls->isInterface() && !cls->isNamespace()) {
            setupInheritance(cls);
        }
    }

    foreach(AbstractMetaClass *cls, m_meta_classes) {
        cls->fixFunctions();

        if (cls->typeEntry() == 0) {
            ReportHandler::warning(QString("class '%1' does not have an entry in the type system")
                                   .arg(cls->name()));
        } else {
            if (!cls->hasConstructors() && !cls->isFinalInCpp() && !cls->isInterface() && !cls->isNamespace())
                cls->addDefaultConstructor();
        }

        if (cls->isAbstract() && !cls->isInterface()) {
            cls->typeEntry()->setLookupName(cls->typeEntry()->targetLangName() + "$ConcreteWrapper");
        }
    }

    QList<TypeEntry *> entries = TypeDatabase::instance()->entries().values();
    foreach(const TypeEntry *entry, entries) {
        if (entry->isPrimitive())
            continue;

        if ((entry->isValue() || entry->isObject())
                && !entry->isString()
                && !entry->isStringRef()
                && !entry->isChar()
                && !entry->isContainer()
                && !entry->isCustom()
                && !entry->isVariant()
                && !m_meta_classes.findClass(entry->qualifiedCppName())) {
            ReportHandler::warning(QString("type '%1' is specified in typesystem, "
                                           "but not defined. This could potentially "
                                           "lead to compilation errors.")
                                   .arg(entry->qualifiedCppName()));
        }

        if (entry->isEnum()) {
            QString pkg = entry->javaPackage();
            QString name = (pkg.isEmpty() ? QString() : pkg + ".")
                           + ((EnumTypeEntry *) entry)->javaQualifier();
            AbstractMetaClass *cls = m_meta_classes.findClass(name);

            if (!cls) {
                ReportHandler::warning(QString("namespace '%1' for enum '%2' is not declared")
                                       .arg(name).arg(entry->targetLangName()));
            } else {
                AbstractMetaEnum *e = cls->findEnum(entry->targetLangName());
                if (!e)
                    ReportHandler::warning(QString("enum '%1' is specified in typesystem, "
                                                   "but not declared")
                                           .arg(entry->qualifiedCppName()));
            }
        }
    }

    {
        FunctionList hash_functions = m_dom->findFunctions("qHash");
        foreach(FunctionModelItem item, hash_functions) {
            registerHashFunction(item);
        }
    }

    {
        FunctionList hash_functions = m_dom->findFunctions("operator<<");
        foreach(FunctionModelItem item, hash_functions) {
            registerToStringCapability(item);
        }
    }

    {
        FunctionList compare_operators = m_dom->findFunctions("operator==")
                                         + m_dom->findFunctions("operator<=")
                                         + m_dom->findFunctions("operator>=")
                                         + m_dom->findFunctions("operator<")
                                         + m_dom->findFunctions("operator>");
        foreach(FunctionModelItem item, compare_operators) {
            traverseCompareOperator(item);
        }
    }

    {
        FunctionList stream_operators = m_dom->findFunctions("operator<<") + m_dom->findFunctions("operator>>");
        foreach(FunctionModelItem item, stream_operators) {
            traverseStreamOperator(item);
        }
    }

    figureOutEnumValues();
    figureOutDefaultEnumArguments();
    checkFunctionModifications();

    foreach(AbstractMetaClass *cls, m_meta_classes) {
        setupEquals(cls);
        setupComparable(cls);
        setupClonable(cls);
    }

    dumpLog();

    sortLists();

    return true;
}


void AbstractMetaBuilder::addAbstractMetaClass(AbstractMetaClass *cls) {
    if (!cls)
        return;

    cls->setOriginalAttributes(cls->attributes());
    if (cls->typeEntry()->isContainer()) {
        m_templates << cls;
    } else {
        m_meta_classes << cls;
        if (cls->typeEntry()->designatedInterface()) {
            AbstractMetaClass *interface = cls->extractInterface();
            m_meta_classes << interface;
            ReportHandler::debugSparse(QString(" -> interface '%1'").arg(interface->name()));
        }
    }
}


AbstractMetaClass *AbstractMetaBuilder::traverseNamespace(NamespaceModelItem namespace_item) {
    QString namespace_name = (!m_namespace_prefix.isEmpty() ? m_namespace_prefix + "::" : QString()) + namespace_item->name();

    NamespaceTypeEntry *type = TypeDatabase::instance()->findNamespaceType(namespace_name);

    if (TypeDatabase::instance()->isClassRejected(namespace_name)) {
        m_rejected_classes.insert(namespace_name, GenerationDisabled);
        return 0;
    }

    if (!type) {
        ReportHandler::warning(QString("namespace '%1' does not have a type entry")
                               .arg(namespace_name));
        return 0;
    }

    AbstractMetaClass *meta_class = createMetaClass();
    meta_class->setTypeEntry(type);

    *meta_class += AbstractMetaAttributes::Public;

    m_current_class = meta_class;

    ReportHandler::debugSparse(QString("namespace '%1.%2'")
                               .arg(meta_class->package())
                               .arg(namespace_item->name()));

    traverseEnums(model_dynamic_cast<ScopeModelItem>(namespace_item), meta_class, namespace_item->enumsDeclarations());
    traverseFunctions(model_dynamic_cast<ScopeModelItem>(namespace_item), meta_class);
//     traverseClasses(model_dynamic_cast<ScopeModelItem>(namespace_item));

    pushScope(model_dynamic_cast<ScopeModelItem>(namespace_item));
    m_namespace_prefix = currentScope()->qualifiedName().join("::");


    ClassList classes = namespace_item->classes();
    foreach(ClassModelItem cls, classes) {
        AbstractMetaClass *mjc = traverseClass(cls);
        // the classes inside of a namespace are realized as static member classes
        // of the namespace representing java interface.
        if (mjc) {
            meta_class->addEnclosedClass(mjc);
            m_meta_classes << mjc;
            if (mjc->typeEntry()->designatedInterface()) {
                AbstractMetaClass *interface = mjc->extractInterface();
                meta_class->addEnclosedClass(interface);
                m_meta_classes << interface;
                ReportHandler::debugSparse(QString(" -> interface '%1'").arg(interface->name()));
            }
        }
    }

    // Go through all typedefs to see if we have defined any
    // specific typedefs to be used as classes.
    TypeAliasList typeAliases = namespace_item->typeAliases();
    foreach(TypeAliasModelItem typeAlias, typeAliases) {
        AbstractMetaClass *cls = traverseTypeAlias(typeAlias);
        // the classes inside of a namespace are realized as static member classes
        // of the namespace representing java interface.
        if (cls) {
            meta_class->addEnclosedClass(cls);
            m_meta_classes << cls;
            if (cls->typeEntry()->designatedInterface()) {
                AbstractMetaClass *interface = cls->extractInterface();
                meta_class->addEnclosedClass(interface);
                m_meta_classes << interface;
                ReportHandler::debugSparse(QString(" -> interface '%1'").arg(interface->name()));
            }
        }
    }


    // Traverse namespaces recursively
    QList<NamespaceModelItem> inner_namespaces = namespace_item->namespaceMap().values();
    foreach(const NamespaceModelItem &ni, inner_namespaces) {
        AbstractMetaClass *mjc = traverseNamespace(ni);
        // the namespace inside of a namespace are realized as static member interfaces
        // of the namespace representing java interface.
        if (mjc) {
            meta_class->addEnclosedClass(mjc);
            m_meta_classes << mjc;
        }
    }

    m_current_class = 0;


    popScope();
    m_namespace_prefix = currentScope()->qualifiedName().join("::");

    if (!type->include().isValid()) {
        QFileInfo info(namespace_item->fileName());
        type->setInclude(Include(Include::IncludePath, info.fileName()));
    }

    return meta_class;
}

struct Operator {
    enum Type { Plus, ShiftLeft, None };

    Operator() : type(None) { }

    int calculate(int x) {
        switch (type) {
            case Plus: return x + value;
            case ShiftLeft: return x << value;
            case None: return x;
        }
        return x;
    }

    Type type;
    int value;
};



Operator findOperator(QString *s) {
    const char *names[] = {
        "+",
        "<<"
    };

    for (int i = 0; i < Operator::None; ++i) {
        QString name = QLatin1String(names[i]);
        QString str = *s;
        int splitPoint = str.indexOf(name);
        if (splitPoint > 0) {
            bool ok;
            QString right = str.mid(splitPoint + name.length());
            Operator op;
            op.value = right.toInt(&ok);
            if (ok) {
                op.type = Operator::Type(i);
                *s = str.left(splitPoint).trimmed();
                return op;
            }
        }
    }
    return Operator();
}

int AbstractMetaBuilder::figureOutEnumValue(const QString &origStringValue,
        int oldValuevalue,
        AbstractMetaEnum *meta_enum,
        AbstractMetaFunction *meta_function) {
    if (origStringValue.isEmpty())
        return oldValuevalue;

    QString stringValue(origStringValue);

    // This block deals with "static_cast<FooBar::Type>" prefix on cpp defaulted values
    const QString keyword_static_cast("static_cast");
    if (stringValue.startsWith(keyword_static_cast)) {
        stringValue = stringValue.remove(0, keyword_static_cast.length()).trimmed();
        if (stringValue.length() > 0 && stringValue.at(0) == QChar('<')) {
            int end_pos = stringValue.indexOf(QChar('>'));
            if (end_pos >= 0)	// remove the whole "<FooBar::Type>"
                stringValue = stringValue.remove(0, end_pos).trimmed();
        }
    }
    // This block deals with "FooBar::Type(.....)" around the part we really want "....."
    {
        int beg_pos = stringValue.indexOf(QChar('('));
        if (beg_pos >= 0) {
            stringValue = stringValue.remove(0, beg_pos+1).trimmed();	// remove "FooBar::Type("

            int end_pos = stringValue.lastIndexOf(QChar(')'));
            if (end_pos >= 0)
                stringValue = stringValue.remove(end_pos, 1).trimmed();	// remove ")"
        }
    }

    QStringList stringValues = stringValue.split("|");

    int returnValue = 0;

    bool matched = false;

    for (int i = 0; i < stringValues.size(); ++i) {
        QString s = stringValues.at(i).trimmed();

        bool ok;
        int v;

        Operator op = findOperator(&s);

        if (s.length() > 0 && s.at(0) == QLatin1Char('0'))
            v = s.toUInt(&ok, 0);
        else
            v = s.toInt(&ok);

        if (ok) {
            matched = true;

        } else if (m_enum_values.contains(s)) {
            AbstractMetaEnumValue *ev = m_enum_values[s];
            // if target value has not yet been computed do it
            if(!ev->isValueSet()){
                if (stringValue.contains("::")) {
                    QString ownerTypeName = stringValue.left(stringValue.indexOf("::"));
                    AbstractMetaClass * ownerType = classes().findClass(ownerTypeName);
                    if(ownerType){
                        AbstractMetaEnum* target_meta_enum = ownerType->findEnumForValue(ev->name());
                        v = figureOutEnumValue(ev->stringValue(), target_meta_enum->values().indexOf(ev), target_meta_enum);
                        ev->setValue(v);
                    }else{
                        ReportHandler::warning("Enum constant belongs to unknown type "+ownerTypeName);
                    }
                }
            }else{
                v = ev->value();
            }
            matched = true;

        } else {
            AbstractMetaEnumValue *ev = 0;

            if (meta_enum && (ev = meta_enum->values().find(s))) {
                // if target value has not yet been computed do it
                if(!ev->isValueSet()){
                    v = figureOutEnumValue(ev->stringValue(), meta_enum->values().indexOf(ev), meta_enum);
                    ev->setValue(v);
                }else{
                    v = ev->value();
                }
                matched = true;

            } else if (meta_enum && (ev = meta_enum->enclosingClass()->findEnumValue(s, meta_enum))) {
                // if target value has not yet been computed do it
                if(!ev->isValueSet()){
                    AbstractMetaEnum* target_meta_enum = meta_enum->enclosingClass()->findEnumForValue(ev->name());
                    v = figureOutEnumValue(ev->stringValue(), target_meta_enum->values().indexOf(ev), target_meta_enum);
                    ev->setValue(v);
                }else{
                    v = ev->value();
                }
                matched = true;

            } else {
                if (meta_enum)
                    ReportHandler::warning("unhandled enum value: " + s + " in "
                                           + meta_enum->enclosingClass()->name() + "::"
                                           + meta_enum->name());
                else
                    ReportHandler::warning("unhandled enum value: Unknown enum");
            }
        }

        if (matched)
            returnValue |= op.calculate(v);
    }

    if (!matched) {
        QString warn = QString("unmatched enum %1").arg(stringValue);

        if (meta_function != 0) {
            warn += QString(" when parsing default value of '%1' in class '%2'")
                    .arg(meta_function->name())
                    .arg(meta_function->implementingClass()->name());
        }

        ReportHandler::warning(warn);
        returnValue = oldValuevalue;
    }

    return returnValue;
}

void AbstractMetaBuilder::figureOutEnumValuesForClass(AbstractMetaClass *meta_class,
        QSet<AbstractMetaClass *> *classes) {
    AbstractMetaClass *base = meta_class->baseClass();

    if (base != 0 && !classes->contains(base))
        figureOutEnumValuesForClass(base, classes);

    if (classes->contains(meta_class))
        return;

    AbstractMetaEnumList enums = meta_class->enums();
    foreach(AbstractMetaEnum *e, enums) {
        if (!e)
            ReportHandler::warning("bad enum in class " + meta_class->name());
        AbstractMetaEnumValueList lst = e->values();
        int value = 0;
        for (int i = 0; i < lst.size(); ++i) {
            // value could have been computed previously
            if(!lst.at(i)->isValueSet()){
                value = figureOutEnumValue(lst.at(i)->stringValue(), value, e);
                lst.at(i)->setValue(value);
            }else{
                value = lst.at(i)->value();
            }
            value++;
        }

        // Check for duplicate values...
        EnumTypeEntry *ete = e->typeEntry();
        if (!ete->forceInteger()) {
            QHash<int, AbstractMetaEnumValue *> entries;
            foreach(AbstractMetaEnumValue *v, lst) {

                bool vRejected = ete->isEnumValueRejected(v->name());

                AbstractMetaEnumValue *current = entries.value(v->value());
                if (current) {
                    bool currentRejected = ete->isEnumValueRejected(current->name());
                    if (!currentRejected && !vRejected) {
                        ReportHandler::warning(
                            QString("duplicate enum values: %1::%2, %3 and %4 are %5, already rejected: (%6)")
                            .arg(meta_class->name())
                            .arg(e->name())
                            .arg(v->name())
                            .arg(entries[v->value()]->name())
                            .arg(v->value())
                            .arg(ete->enumValueRejections().join(", ")));
                        continue;
                    }
                }

                if (!vRejected)
                    entries[v->value()] = v;
            }

            // Entries now contain all the original entries, no
            // rejected ones... Use this to generate the enumValueRedirection table.
            foreach(AbstractMetaEnumValue *reject, lst) {
                if (!ete->isEnumValueRejected(reject->name()))
                    continue;

                AbstractMetaEnumValue *used = entries.value(reject->value());
                if (!used) {
                    ReportHandler::warning(
                        QString::fromLatin1("Rejected enum has no alternative...: %1::%2\n")
                        .arg(meta_class->name())
                        .arg(reject->name()));
                    continue;
                }
                ete->addEnumValueRedirection(reject->name(), used->name());
            }

        }
    }



    *classes += meta_class;
}


void AbstractMetaBuilder::figureOutEnumValues() {
    // Keep a set of classes that we already traversed. We use this to
    // enforce that we traverse base classes prior to subclasses.
    QSet<AbstractMetaClass *> classes;
    foreach(AbstractMetaClass *c, m_meta_classes) {
        figureOutEnumValuesForClass(c, &classes);
    }
}

void AbstractMetaBuilder::figureOutDefaultEnumArguments() {
    foreach(AbstractMetaClass *meta_class, m_meta_classes) {
        foreach(AbstractMetaFunction *meta_function, meta_class->functions()) {
            foreach(AbstractMetaArgument *arg, meta_function->arguments()) {

                QString expr = arg->defaultValueExpression();
                if (expr.isEmpty())
                    continue;

                if (!meta_function->replacedDefaultExpression(meta_function->implementingClass(),
                        arg->argumentIndex() + 1).isEmpty()) {
                    continue;
                }

                QString new_expr = expr;
                if (arg->type()->isEnum()) {
                    QStringList lst = expr.split(QLatin1String("::"));
                    if (lst.size() == 1) {
                        QVector<AbstractMetaClass *> classes(1, meta_class);
                        AbstractMetaEnum *e = 0;
                        while (!classes.isEmpty() && e == 0) {
                            if (classes.front() != 0) {
                                classes << classes.front()->baseClass();

                                AbstractMetaClassList interfaces = classes.front()->interfaces();
                                foreach(AbstractMetaClass *interface, interfaces)
                                    classes << interface->primaryInterfaceImplementor();

                                e = classes.front()->findEnumForValue(expr);
                            }

                            classes.pop_front();
                        }

                        if (e != 0) {
                            new_expr = QString("%1.%2")
                                       .arg(e->typeEntry()->qualifiedTargetLangName())
                                       .arg(expr);
                        } else {
                            ReportHandler::warning("Cannot find enum constant for value '" + expr + "' in '" + meta_class->name() + "' or any of its super classes");
                        }
                    } else if (lst.size() == 2) {
                        AbstractMetaClass *cl = m_meta_classes.findClass(lst.at(0));
                        if (!cl) {
                            ReportHandler::warning("missing required class for enums: " + lst.at(0));
                            continue;
                        }
                        new_expr = QString("%1.%2.%3")
                                   .arg(cl->typeEntry()->qualifiedTargetLangName())
                                   .arg(arg->type()->name())
                                   .arg(lst.at(1));
                    } else {
                        ReportHandler::warning("bad default value passed to enum " + expr);
                    }

                } else if (arg->type()->isFlags()) {
                    const FlagsTypeEntry *flagsEntry =
                        static_cast<const FlagsTypeEntry *>(arg->type()->typeEntry());
                    EnumTypeEntry *enumEntry = flagsEntry->originator();
                    AbstractMetaEnum *meta_enum = m_meta_classes.findEnum(enumEntry);
                    if (!meta_enum) {
                        ReportHandler::warning("unknown required enum " + enumEntry->qualifiedCppName());
                        continue;
                    }

                    int value = figureOutEnumValue(expr, 0, meta_enum, meta_function);
                    new_expr = QString::number(value);

                } else if (arg->type()->isPrimitive()) {
                    AbstractMetaEnumValue *value = 0;
                    if (expr.contains("::"))
                        value = m_meta_classes.findEnumValue(expr);
                    if (!value)
                        value = meta_class->findEnumValue(expr, 0);

                    if (value) {
                        new_expr = QString::number(value->value());
                    } else if (expr.contains(QLatin1Char('+'))) {
                        new_expr = QString::number(figureOutEnumValue(expr, 0, 0));

                    }



                }

                arg->setDefaultValueExpression(new_expr);
            }
        }
    }
}


AbstractMetaEnum *AbstractMetaBuilder::traverseEnum(EnumModelItem enum_item, AbstractMetaClass *enclosing, const QSet<QString> &enumsDeclarations) {
    // Skipping private enums.
    if (enum_item->accessPolicy() == CodeModel::Private) {
        return 0;
    }

    QString qualified_name = enum_item->qualifiedName().join("::");

    TypeEntry *type_entry = TypeDatabase::instance()->findType(qualified_name);
    QString enum_name = enum_item->name();

    QString class_name;
    if (m_current_class)
        class_name = m_current_class->typeEntry()->qualifiedCppName();

    if (TypeDatabase::instance()->isEnumRejected(class_name, enum_name)) {
        m_rejected_enums.insert(qualified_name, GenerationDisabled);
        return 0;
    }

    if (!type_entry || !type_entry->isEnum()) {
        QString context = m_current_class ? m_current_class->name() : QLatin1String("");
        ReportHandler::warning(QString("enum '%1' does not have a type entry or is not an enum")
                               .arg(qualified_name));
        m_rejected_enums.insert(qualified_name, NotInTypeSystem);
        return 0;
    }

    AbstractMetaEnum *meta_enum = createMetaEnum();
    if (enumsDeclarations.contains(qualified_name)
            || enumsDeclarations.contains(enum_name)) {
        meta_enum->setHasQEnumsDeclaration(true);
    }

    meta_enum->setTypeEntry((EnumTypeEntry *) type_entry);
    switch (enum_item->accessPolicy()) {
        case CodeModel::Public: *meta_enum += AbstractMetaAttributes::Public; break;
        case CodeModel::Protected: *meta_enum += AbstractMetaAttributes::Protected; break;
//     case CodeModel::Private: *meta_enum += AbstractMetaAttributes::Private; break;
        default: break;
    }

    ReportHandler::debugMedium(QString(" - traversing enum %1").arg(meta_enum->fullName()));

    foreach(EnumeratorModelItem value, enum_item->enumerators()) {

        AbstractMetaEnumValue *meta_enum_value = createMetaEnumValue();
        meta_enum_value->setName(value->name());
        // Deciding the enum value...

        meta_enum_value->setStringValue(value->value());
        meta_enum->addEnumValue(meta_enum_value);

        ReportHandler::debugFull("   - " + meta_enum_value->name() + " = "
                                 + meta_enum_value->value());

        // Add into global register...
        if (enclosing)
            m_enum_values[enclosing->name() + "::" + meta_enum_value->name()] = meta_enum_value;
        else
            m_enum_values[meta_enum_value->name()] = meta_enum_value;
    }

    m_enums << meta_enum;

    return meta_enum;
}

AbstractMetaClass *AbstractMetaBuilder::traverseTypeAlias(TypeAliasModelItem typeAlias) {
    QString class_name = strip_template_args(typeAlias->name());

    QString full_class_name = class_name;
    // we have an inner class
    if (m_current_class) {
        full_class_name = strip_template_args(m_current_class->typeEntry()->qualifiedCppName())
                          + "::" + full_class_name;
    }

    // If we haven't specified anything for the typedef, then we don't care
    ComplexTypeEntry *type = TypeDatabase::instance()->findComplexType(full_class_name);
    if (type == 0)
        return 0;

    if (type->isObject())
        static_cast<ObjectTypeEntry *>(type)->setQObject(isQObject(strip_template_args(typeAlias->type().qualifiedName().join("::"))));

    AbstractMetaClass *meta_class = createMetaClass();
    meta_class->setTypeAlias(true);
    meta_class->setTypeEntry(type);
    meta_class->setBaseClassNames(QStringList() << typeAlias->type().qualifiedName().join("::"));
    *meta_class += AbstractMetaAttributes::Public;

    // Set the default include file name
    if (!type->include().isValid()) {
        QFileInfo info(typeAlias->fileName());
        type->setInclude(Include(Include::IncludePath, info.fileName()));
    }

    return meta_class;
}

AbstractMetaClass *AbstractMetaBuilder::traverseClass(ClassModelItem class_item) {
    QString class_name = strip_template_args(class_item->name());
    QString full_class_name = class_name;

    // we have inner an class
    if (m_current_class) {
        full_class_name = strip_template_args(m_current_class->typeEntry()->qualifiedCppName())
                          + "::" + full_class_name;
    }

    ComplexTypeEntry *type = TypeDatabase::instance()->findComplexType(full_class_name);
    RejectReason reason = NoReason;

    if (TypeDatabase::instance()->isClassRejected(full_class_name)) {
        reason = GenerationDisabled;
    } else if (!type) {
        TypeEntry *te = TypeDatabase::instance()->findType(full_class_name);
        if (te && !te->isComplex())
            reason = RedefinedToNotClass;
        else
            reason = NotInTypeSystem;
    } else if (type->codeGeneration() == TypeEntry::GenerateNothing) {
        reason = GenerationDisabled;
    }

    if (reason != NoReason) {
        m_rejected_classes.insert(full_class_name, reason);
        return 0;
    }

    if (type->isObject()) {
        ((ObjectTypeEntry *)type)->setQObject(isQObject(full_class_name));
    }

    AbstractMetaClass *meta_class = createMetaClass();
    meta_class->setTypeEntry(type);
    meta_class->setBaseClassNames(class_item->baseClasses());
    *meta_class += AbstractMetaAttributes::Public;

    AbstractMetaClass *old_current_class = m_current_class;
    m_current_class = meta_class;

    if (type->isContainer()) {
        ReportHandler::debugSparse(QString("container: '%1'").arg(full_class_name));
    } else {
        ReportHandler::debugSparse(QString("class: '%1'").arg(meta_class->fullName()));
    }

    TemplateParameterList template_parameters = class_item->templateParameters();
    QList<TypeEntry *> template_args;
    template_args.clear();
    for (int i = 0; i < template_parameters.size(); ++i) {
        const TemplateParameterModelItem &param = template_parameters.at(i);
        TemplateArgumentEntry *param_type = new TemplateArgumentEntry(param->name());
        param_type->setOrdinal(i);
        template_args.append(param_type);
    }
    meta_class->setTemplateArguments(template_args);

    parseQ_Property(meta_class, class_item->propertyDeclarations());

    traverseFunctions(model_dynamic_cast<ScopeModelItem>(class_item), meta_class);
    traverseEnums(model_dynamic_cast<ScopeModelItem>(class_item), meta_class, class_item->enumsDeclarations());
    traverseFields(model_dynamic_cast<ScopeModelItem>(class_item), meta_class);

    // Inner classes
    {
        QList<ClassModelItem> inner_classes = class_item->classMap().values();
        foreach(const ClassModelItem &ci, inner_classes) {
            AbstractMetaClass *cl = traverseClass(ci);
            if (cl) {
                meta_class->addEnclosedClass(cl);
                m_meta_classes << cl;
                if (cl->typeEntry()->designatedInterface()) {
                    AbstractMetaClass *interface = cl->extractInterface();
                    meta_class->addEnclosedClass(interface);
                    m_meta_classes << interface;
                    ReportHandler::debugSparse(QString(" -> interface '%1'").arg(interface->name()));
                }
            }
        }
    }

    // Go through all typedefs to see if we have defined any
    // specific typedefs to be used as classes.
    TypeAliasList typeAliases = class_item->typeAliases();
    foreach(TypeAliasModelItem typeAlias, typeAliases) {
        AbstractMetaClass *cls = traverseTypeAlias(typeAlias);
        if (cls != 0) {
            meta_class->addEnclosedClass(cls);
            addAbstractMetaClass(cls);
        }
    }


    m_current_class = old_current_class;

    // Set the default include file name
    if (!type->include().isValid()) {
        QFileInfo info(class_item->fileName());
        type->setInclude(Include(Include::IncludePath, info.fileName()));
    }
    // not possible to inherit from a union type, so declare final
    if(class_item.data()->classType()==CodeModel::Union){
        *meta_class += AbstractMetaAttributes::Final;
    }

    return meta_class;
}

AbstractMetaField *AbstractMetaBuilder::traverseField(VariableModelItem field, const AbstractMetaClass *cls) {
    QString field_name = field->name();
    QString class_name = m_current_class->typeEntry()->qualifiedCppName();

    //qDebug()<<"\n"<<field_name<<class_name;

    // Ignore friend decl.
    if (field->isFriend())
        return 0;

    if (field->accessPolicy() == CodeModel::Private)
        return 0;

    if (TypeDatabase::instance()->isFieldRejected(class_name, field_name)) {
        m_rejected_fields.insert(class_name + "::" + field_name, GenerationDisabled);
        return 0;
    }

    AbstractMetaField *meta_field = createMetaField();
    meta_field->setName(field_name);
    meta_field->setEnclosingClass(cls);

    bool ok;
    TypeInfo field_type = field->type();
    //qDebug()<<"\n\n\n"<<"Class in question:"<<cls->name()<<"\n\n\n";
    AbstractMetaType *meta_type = translateType(field_type, &ok, "traverseField " + class_name);

    if (!meta_type || !ok) {
        QString error = QString("skipping field '%1::%2' with unmatched type '%3'")
                        .arg(m_current_class->name())
                        .arg(field_name)
                        .arg(TypeInfo::resolveType(field_type, currentScope()->toItem()).qualifiedName().join("::"));
        ReportHandler::warning(error);
        delete meta_field;
        return 0;
    }

    meta_field->setType(meta_type);

    uint attr = 0;
    if (field->isStatic())
        attr |= AbstractMetaAttributes::Static;

    CodeModel::AccessPolicy policy = field->accessPolicy();
    if (policy == CodeModel::Public)
        attr |= AbstractMetaAttributes::Public;
    else if (policy == CodeModel::Protected)
        attr |= AbstractMetaAttributes::Protected;
    else
        attr |= AbstractMetaAttributes::Private;
    meta_field->setAttributes(attr);

    return meta_field;
}

void AbstractMetaBuilder::traverseFields(ScopeModelItem scope_item, AbstractMetaClass *meta_class) {
    foreach(VariableModelItem field, scope_item->variables()) {
        AbstractMetaField *meta_field = traverseField(field, meta_class);

        if (meta_field) {
            meta_field->setOriginalAttributes(meta_field->attributes());
            meta_class->addField(meta_field);
        }
    }
}

void AbstractMetaBuilder::setupFunctionDefaults(AbstractMetaFunction *meta_function, AbstractMetaClass *meta_class) {
    // Set the default value of the declaring class. This may be changed
    // in fixFunctions later on
    meta_function->setDeclaringClass(meta_class);

    // Some of the queries below depend on the implementing class being set
    // to function properly. Such as function modifications
    meta_function->setImplementingClass(meta_class);

    if (meta_function->name() == "operator_equal")
        meta_class->setHasEqualsOperator(true);

    if (!meta_function->isFinalInTargetLang()
            && meta_function->isRemovedFrom(meta_class, TypeSystem::TargetLangCode)) {
        *meta_function += AbstractMetaAttributes::FinalInCpp;
    }
}

void AbstractMetaBuilder::traverseFunctions(ScopeModelItem scope_item, AbstractMetaClass *meta_class) {

    // if there are just private constructors avoid generating the shell class
    bool hasJustPrivateConstructors = false;
    // detect virtual destructor
    bool hasVirtualDestructor = false;
    // information about public descructor is required to detect the deletable supertype of a class
    bool hasPublicDestructor = false;
    // if there is a private descructor don't generate shell class
    bool hasPrivateDestructor = false;

    foreach(FunctionModelItem function, scope_item->functions()) {
        AbstractMetaFunction *meta_function = traverseFunction(function);

        if (meta_function) {

            QList<FunctionModification> mods = meta_function->modifications(meta_class);
            for (int i = 0; i < mods.size(); ++i) {
                if (mods.at(i).isPrivateSignal()) {
                    meta_function->setFunctionType(AbstractMetaFunction::SignalFunction);
                }
            }

            meta_function->setOriginalAttributes(meta_function->attributes());
            if (meta_class->isNamespace())
                *meta_function += AbstractMetaAttributes::Static;

            if (!meta_function->isInvalid()) {
                if (QPropertySpec *read = meta_class->propertySpecForRead(meta_function->name())) {
                    if (read->type() == meta_function->type()->typeEntry()) {
                        *meta_function += AbstractMetaAttributes::PropertyReader;
                        meta_function->setPropertySpec(read);
                    }
                } else if (QPropertySpec *write =
                               meta_class->propertySpecForWrite(meta_function->name())) {
                    if (write->type() == meta_function->arguments().at(0)->type()->typeEntry()) {
                        *meta_function += AbstractMetaAttributes::PropertyWriter;
                        meta_function->setPropertySpec(write);
                    }
                } else if (QPropertySpec *reset =
                               meta_class->propertySpecForReset(meta_function->name())) {
                    *meta_function += AbstractMetaAttributes::PropertyResetter;
                    meta_function->setPropertySpec(reset);
                }
            }

            if(meta_function->isDestructor()){
                hasVirtualDestructor = function.data()->isVirtual();
                hasPrivateDestructor = meta_function->isPrivate();
                hasPublicDestructor = meta_function->isPublic();
            }else if(meta_function->isConstructor()){
                if(meta_function->isPrivate() || meta_function->isInvalid()){
                    AbstractMetaFunctionList functions = meta_class->queryFunctions(AbstractMetaClass::Constructors);
                    // do not generate derived class when only copy constructor is available
                    if(functions.isEmpty() || (functions.size()==1 && functions.at(0)->isCopyConstructor())){
                        hasJustPrivateConstructors = true;
                    }
                }else{
                    hasJustPrivateConstructors = false;
                }
            }

            if (!meta_function->isDestructor()
                    && !meta_function->isInvalid()
                    && (!meta_function->isConstructor() || !meta_function->isPrivate())) {

                if (meta_class->typeEntry()->designatedInterface() && !meta_function->isPublic()
                        && !meta_function->isPrivate()) {
                    QString warn = QString("non-public function '%1' in interface '%2'")
                                   .arg(meta_function->name()).arg(meta_class->name());
                    ReportHandler::warning(warn);

//                  don't make public! just skip protected methods in the interface
                    //meta_function->setVisibility(AbstractMetaClass::Public);
                }

                setupFunctionDefaults(meta_function, meta_class);

                if (meta_function->isSignal() && meta_class->hasSignal(meta_function)) {
                    QString warn = QString("signal '%1' in class '%2' is overloaded.")
                                   .arg(meta_function->name()).arg(meta_class->name());
                    ReportHandler::warning(warn);
                }

                if (meta_function->isSignal() && !meta_class->isQObject()) {
                    QString warn = QString("signal '%1' in non-QObject class '%2'")
                                   .arg(meta_function->name()).arg(meta_class->name());
                    ReportHandler::warning(warn);
                }

                meta_class->addFunction(meta_function);
            }
        }
    }
    if(hasJustPrivateConstructors || hasPrivateDestructor){
        *meta_class += AbstractMetaAttributes::Final;
    }
    meta_class->setHasPublicDestructor(hasPublicDestructor);
    meta_class->setHasPrivateDestructor(hasPrivateDestructor);
    meta_class->setHasVirtualDestructor(hasVirtualDestructor);
    meta_class->setHasJustPrivateConstructors(hasJustPrivateConstructors);
}

bool AbstractMetaBuilder::setupInheritance(AbstractMetaClass *meta_class) {
    Q_ASSERT(!meta_class->isInterface());

    if (m_setup_inheritance_done.contains(meta_class))
        return true;
    m_setup_inheritance_done.insert(meta_class);

    QStringList base_classes = meta_class->baseClassNames();

    TypeDatabase *types = TypeDatabase::instance();

    // we only support our own containers and ONLY if there is only one baseclass
    if (base_classes.size() == 1 && base_classes.first().count('<') == 1) {
        QStringList scope = meta_class->typeEntry()->qualifiedCppName().split("::");
        scope.removeLast();
        for (int i = scope.size(); i >= 0; --i) {
            QString prefix = i > 0 ? QStringList(scope.mid(0, i)).join("::") + "::" : QString();
            QString complete_name = prefix + base_classes.first();
            TypeParser::Info info = TypeParser::parse(complete_name);
            QString base_name = info.qualified_name.join("::");

            AbstractMetaClass *templ = 0;
            foreach(AbstractMetaClass *c, m_templates) {
                if (c->typeEntry()->name() == base_name) {
                    templ = c;
                    break;
                }
            }

            if (templ == 0)
                templ = m_meta_classes.findClass(base_name);

            if (templ) {
                setupInheritance(templ);
                inheritTemplate(meta_class, templ, info);
                return true;
            }
        }

        ReportHandler::warning(QString("template baseclass '%1' of '%2' is not known")
                               .arg(base_classes.first())
                               .arg(meta_class->name()));
        return false;
    }

    int primary = -1;
    int primaries = 0;
    for (int i = 0; i < base_classes.size(); ++i) {

        if (types->isClassRejected(base_classes.at(i)))
            continue;

        TypeEntry *base_class_entry = types->findType(base_classes.at(i));
        if (!base_class_entry) {
            ReportHandler::warning(QString("class '%1' inherits from unknown base class '%2'")
                                   .arg(meta_class->name()).arg(base_classes.at(i)));
        }

        // true for primary base class
        else if (!base_class_entry->designatedInterface()) {
            if (primaries > 0) {
                ReportHandler::warning(QString("class '%1' has multiple primary base classes"
                                               " '%2' and '%3'")
                                       .arg(meta_class->name())
                                       .arg(base_classes.at(primary))
                                       .arg(base_class_entry->name()));
                return false;
            }
            primaries++;
            primary = i;
        }
    }

    if (primary >= 0) {
        AbstractMetaClass *base_class = m_meta_classes.findClass(base_classes.at(primary));
        if (!base_class) {
            ReportHandler::warning(QString("unknown baseclass for '%1': '%2'")
                                   .arg(meta_class->name())
                                   .arg(base_classes.at(primary)));
            return false;
        }
        meta_class->setBaseClass(base_class);

        if (meta_class->typeEntry()->designatedInterface() != 0 && meta_class->isQObject()) {
            ReportHandler::warning(QString("QObject extended by interface type '%1'. This is not supported and the generated Java code will not compile.")
                                   .arg(meta_class->name()));
        } else if (meta_class->typeEntry()->designatedInterface() != 0 && base_class != 0 && !base_class->isInterface()) {
            ReportHandler::warning(QString("object type '%1' extended by interface type '%2'. The resulting API will be less expressive than the original.")
                                   .arg(base_class->name())
                                   .arg(meta_class->name()));
        }

    }

    for (int i = 0; i < base_classes.size(); ++i) {
        if (types->isClassRejected(base_classes.at(i)))
            continue;

        if (i != primary) {
            AbstractMetaClass *base_class = m_meta_classes.findClass(base_classes.at(i));
            if (base_class == 0) {
                ReportHandler::warning(QString("class not found for setup inheritance '%1'").arg(base_classes.at(i)));
                return false;
            }

            setupInheritance(base_class);

            QString interface_name = InterfaceTypeEntry::interfaceName(base_class->name());
            AbstractMetaClass *iface = m_meta_classes.findClass(interface_name);
            if (!iface) {
                ReportHandler::warning(QString("unknown interface for '%1': '%2'")
                                       .arg(meta_class->name())
                                       .arg(interface_name));
                return false;
            }
            meta_class->addInterface(iface);

            AbstractMetaClassList interfaces = iface->interfaces();
            foreach(AbstractMetaClass *iface, interfaces)
                meta_class->addInterface(iface);
        }
    }

    return true;
}

void AbstractMetaBuilder::traverseEnums(ScopeModelItem scope_item, AbstractMetaClass *meta_class, const QStringList &enumsDeclarations) {
    EnumList enums = scope_item->enums();
    foreach(EnumModelItem enum_item, enums) {
        AbstractMetaEnum *meta_enum = traverseEnum(enum_item, meta_class, QSet<QString>::fromList(enumsDeclarations));
        if (meta_enum) {
            meta_enum->setOriginalAttributes(meta_enum->attributes());
            meta_class->addEnum(meta_enum);
            meta_enum->setEnclosingClass(meta_class);
        }
    }
}

AbstractMetaFunction *AbstractMetaBuilder::traverseFunction(FunctionModelItem function_item) {
    QString function_name = function_item->name();
    QString class_name = m_current_class->typeEntry()->qualifiedCppName();

    if (TypeDatabase::instance()->isFunctionRejected(class_name, function_name)) {
        m_rejected_functions.insert(class_name + "::" + function_name, GenerationDisabled);
        return 0;
    }


    Q_ASSERT(function_item->functionType() == CodeModel::Normal
             || function_item->functionType() == CodeModel::Signal
             || function_item->functionType() == CodeModel::Slot);

    if (function_item->isFriend())
        return 0;


    QString cast_type;

    if (function_name.startsWith("operator")) {
        function_name = rename_operator(function_name.mid(8));
        if (function_name.isEmpty()) {
            m_rejected_functions.insert(class_name + "::" + function_name,
                                        GenerationDisabled);
            return 0;
        }
        if (function_name.contains("_cast_"))
            cast_type = function_name.mid(14).trimmed();
    }

    AbstractMetaFunction *meta_function = createMetaFunction();
    meta_function->setConstant(function_item->isConstant());

    ReportHandler::debugMedium(QString(" - %2()").arg(function_name));

    meta_function->setName(function_name);
    meta_function->setOriginalName(function_item->name());

    if (function_item->isAbstract())
        *meta_function += AbstractMetaAttributes::Abstract;

    if (!meta_function->isAbstract())
        *meta_function += AbstractMetaAttributes::Native;

    if (!function_item->isVirtual())
        *meta_function += AbstractMetaAttributes::Final;

    if (function_item->isInvokable())
        *meta_function += AbstractMetaAttributes::Invokable;

    if (function_item->isStatic()) {
        *meta_function += AbstractMetaAttributes::Static;
        *meta_function += AbstractMetaAttributes::Final;
    }

    // Access rights
    if (function_item->accessPolicy() == CodeModel::Public)
        *meta_function += AbstractMetaAttributes::Public;
    else if (function_item->accessPolicy() == CodeModel::Private)
        *meta_function += AbstractMetaAttributes::Private;
    else
        *meta_function += AbstractMetaAttributes::Protected;


    QString stripped_class_name = class_name;
    int cc_pos = stripped_class_name.lastIndexOf("::");
    if (cc_pos > 0)
        stripped_class_name = stripped_class_name.mid(cc_pos + 2);

    TypeInfo function_type = function_item->type();
    if (function_name.startsWith('~')) {
        meta_function->setFunctionType(AbstractMetaFunction::DestructorFunction);
        meta_function->setInvalid(true);
    } else if (strip_template_args(function_name) == stripped_class_name) {
        meta_function->setFunctionType(AbstractMetaFunction::ConstructorFunction);
        meta_function->setName(m_current_class->simpleName());
    } else {
        bool ok;
        AbstractMetaType *type = 0;

        if (!cast_type.isEmpty()) {
            TypeInfo info;
            info.setQualifiedName(QStringList(cast_type));
            type = translateType(info, &ok, QString("traverseField %1.%2").arg(class_name).arg(function_name));
        } else {
            type = translateType(function_type, &ok, QString("traverseField %1.%2").arg(class_name).arg(function_name));
        }

        if (!ok) {
            ReportHandler::warning(QString("skipping function '%1::%2', unmatched return type '%3'")
                                   .arg(class_name)
                                   .arg(function_item->name())
                                   .arg(function_item->type().toString()));
            m_rejected_functions[class_name + "::" + function_name] =
                UnmatchedReturnType;
            meta_function->setInvalid(true);
            return meta_function;
        }
        meta_function->setType(type);

        if (function_item->functionType() == CodeModel::Signal)
            meta_function->setFunctionType(AbstractMetaFunction::SignalFunction);
        else if (function_item->functionType() == CodeModel::Slot)
            meta_function->setFunctionType(AbstractMetaFunction::SlotFunction);
    }

    ArgumentList arguments = function_item->arguments();
    AbstractMetaArgumentList meta_arguments;

    int first_default_argument = 0;
    for (int i = 0; i < arguments.size(); ++i) {
        ArgumentModelItem arg = arguments.at(i);

        bool ok;
        AbstractMetaType *meta_type = translateType(arg->type(), &ok, QString("traverseField %1.%2 arg#%3").arg(class_name).arg(function_name).arg(i));
        if (!meta_type || !ok) {
            ReportHandler::warning(QString("skipping function '%1::%2', "
                                           "unmatched parameter type '%3'")
                                   .arg(class_name)
                                   .arg(function_item->name())
                                   .arg(arg->type().toString()));
            m_rejected_functions[class_name + "::" + function_name] =
                UnmatchedArgumentType;
            meta_function->setInvalid(true);
            return meta_function;
        }
        AbstractMetaArgument *meta_argument = createMetaArgument();
        meta_argument->setType(meta_type);
        meta_argument->setName(arg->name());
        meta_argument->setArgumentIndex(i);
        meta_arguments << meta_argument;
    }

    meta_function->setArguments(meta_arguments);

    // Find the correct default values
    for (int i = 0; i < arguments.size(); ++i) {
        ArgumentModelItem arg = arguments.at(i);
        AbstractMetaArgument *meta_arg = meta_arguments.at(i);
        if (arg->defaultValue()) {
            QString expr = arg->defaultValueExpression();
            if (!expr.isEmpty())
                meta_arg->setOriginalDefaultValueExpression(expr);

            expr = translateDefaultValue(arg, meta_arg->type(), meta_function, m_current_class, i);
            if (expr.isEmpty()) {
                first_default_argument = i;
            } else {
                meta_arg->setDefaultValueExpression(expr);
            }

            if (meta_arg->type()->isEnum() || meta_arg->type()->isFlags()) {
                m_enum_default_arguments
                << QPair<AbstractMetaArgument *, AbstractMetaFunction *>(meta_arg, meta_function);
            }

        }
    }

    // If we where not able to translate the default argument make it
    // reset all default arguments before this one too.
    for (int i = 0; i < first_default_argument; ++i)
        meta_arguments[i]->setDefaultValueExpression(QString());

    if (ReportHandler::debugLevel() == ReportHandler::FullDebug) {
        foreach(AbstractMetaArgument *arg, meta_arguments)
            ReportHandler::debugFull("   - " + arg->toString());
    }

    return meta_function;
}


// uncomment the qDebug()s in order to inspect internals of the function
AbstractMetaType *AbstractMetaBuilder::translateType(const TypeInfo &type_info,
        bool *ok,
        const QString &contextString,
        bool resolveType,
        bool resolveScope) {
    Q_ASSERT(ok);
    *ok = true;
    //qDebug()<<"Start of translateType()"<<type_info.toString();

    // 1. Test the type info without resolving typedefs in case this is present in the
    //    type system

    TypeInfo typei;
    if (resolveType) {
        bool ok;
        AbstractMetaType *t = translateType(type_info, &ok, contextString, false, resolveScope);
        if (t != 0 && ok) {
            //qDebug()<<"ResolveType ok (1. check)"<<t->fullName();
            return t;
        }
    }

    if (!resolveType)
        typei = type_info;
    else {
        // Go through all parts of the current scope (including global namespace)
        // to resolve typedefs. The parser does not properly resolve typedefs in
        // the global scope when they are referenced from inside a namespace.
        // This is a work around to fix this bug since fixing it in resolveType
        // seemed non-trivial
        int i = m_scopes.size() - 1;
        while (i >= 0) {
            typei = TypeInfo::resolveType(type_info, m_scopes.at(i--)->toItem());
            if (typei.qualifiedName().join("::") != type_info.qualifiedName().join("::"))
                break;
        }
        //qDebug()<<"Resolved:"<<typei.toString();
    }

    if (typei.isFunctionPointer()) {
        *ok = false;
        ReportHandler::warning("isFunctionPointer: "+type_info.toString());
        return 0;
    }

    TypeParser::Info typeInfo = TypeParser::parse(typei.toString());

    if (typeInfo.is_busted) {
        *ok = false;
        qDebug() << "Type parser doesn’t recognize the type (is_busted)";
        return 0;
    }

    //qDebug()<<"TypeInfo is after 1. method:"<<typeInfo.toString();

    // 2. Handle pointers specified as arrays with unspecified size

    bool array_of_unspecified_size = false;
    if (typeInfo.arrays.size() > 0) {
        array_of_unspecified_size = true;
        for (int i = 0; i < typeInfo.arrays.size(); ++i)
            array_of_unspecified_size = array_of_unspecified_size && typeInfo.arrays.at(i).isEmpty();

        if (!array_of_unspecified_size) {
            TypeInfo newInfo;
            //newInfo.setArguments(typei.arguments());
            newInfo.setIndirections(typei.indirections());
            newInfo.setConstant(typei.isConstant());
            newInfo.setFunctionPointer(typei.isFunctionPointer());
            newInfo.setQualifiedName(typei.qualifiedName());
            newInfo.setReference(typei.isReference());
            newInfo.setVolatile(typei.isVolatile());

            AbstractMetaType *elementType = translateType(newInfo, ok, contextString);
            if (!ok) {
                qDebug() << "Something has happened.";
                return 0;
            }

            for (int i = typeInfo.arrays.size() - 1; i >= 0; --i) {
                QString s = typeInfo.arrays.at(i);
                bool ok;

                int elems = s.toInt(&ok);
                if (!ok) {
                    qDebug() << "Something has happened.";
                    return 0;
                }

                AbstractMetaType *arrayType = createMetaType();
                arrayType->setArrayElementCount(elems);
                arrayType->setArrayElementType(elementType);
                arrayType->setTypeEntry(new ArrayTypeEntry(elementType->typeEntry()));
                decideUsagePattern(arrayType, contextString);

                elementType = arrayType;
            }

            //qDebug()<<"Returning element type:"<<elementType->fullName();
            return elementType;
        }  else {
            for(int i=0; i<typeInfo.arrays.size(); i++){
                typeInfo.indirections << false;
            }
        }
    }

    QStringList qualifier_list = typeInfo.qualified_name;
    if (qualifier_list.isEmpty()) {
        ReportHandler::warning(QString("horribly broken type '%1'").arg(type_info.toString()));
        *ok = false;
        return 0;
    }

    QString qualified_name = qualifier_list.join("::");
    QString name = qualifier_list.takeLast();

    // 3. Special case 'void' type
    if (name == "void" && typeInfo.indirections.size() == 0) {
        //qDebug()<<"Returning from name == \"void\" && typeInfo.indirections == 0";
        return 0;
    }

    // 4. Special case QFlags (include instantiation in name)
    if (qualified_name == "QFlags")
        qualified_name = typeInfo.toString();

    // 5. Try to find the type
    const TypeEntry *type = TypeDatabase::instance()->findType(qualified_name);

    if (type)
        //qDebug()<<"After 5. name: " << type->name();

        // 6. No? Try looking it up as a flags type
        if (!type)
            type = TypeDatabase::instance()->findFlagsType(qualified_name);

    // 7. No? Try looking it up as a container type
    if (!type)
        type = TypeDatabase::instance()->findContainerType(name);

    // 8. No? Check if the current class is a template and this type is one
    //    of the parameters.
    if (type == 0 && m_current_class != 0) {
        QList<TypeEntry *> template_args = m_current_class->templateArguments();
        foreach(TypeEntry *te, template_args) {
            if (te->name() == qualified_name)
                type = te;
        }
    }

    ClassModelItem containing_class = m_dom->findClass(qualifier_list.join("::"));

    // 9. Try finding the type by prefixing it with
    //     all baseclasses of the containing class
    if (!type && !TypeDatabase::instance()->isClassRejected(qualified_name) && containing_class != 0 && resolveScope) {
        QStringList contexts;
        contexts.append(containing_class->baseClasses());

        // qDebug() << "9."<< contexts;

        TypeInfo info = typei;
        while (!contexts.isEmpty() && type == 0) {
            //type = TypeDatabase::instance()->findType(contexts.at(0) + "::" + qualified_name);

            bool ok;
            info.setQualifiedName(QStringList() << contexts.at(0) << name);
            //   qDebug()<< "whiling in 9. type," << info.toString();
            AbstractMetaType *t = translateType(info, &ok, contextString, true, false);
            if (t != 0 && ok) {
                //     qDebug()<<"Returning ok from t != 0 && ok"<<t->fullName();
                return t;
            }

            //10. Try if the type is in a base class
            //e.g. MimeType is QWebPluginFactory::MimeType and called by QWebPluginFactory::Plugin
            QString base = contexts.at(0);
            QStringList parts = base.split("::");
            while (parts.size() > 1) {
                parts.removeLast();
                info.setQualifiedName(QStringList() << parts << name);
                AbstractMetaType *t = translateType(info, &ok, contextString, true, false);
                if (t != 0 && ok) {
                    //       qDebug()<<"Returning ok from 11. method"<<t->fullName();
                    return t;
                }
            }

            ClassModelItem item = m_dom->findClass(contexts.at(0));
            if (item != 0) {
                contexts += item->baseClasses();
            }
            contexts.removeFirst();
        }
    }

    // 12. Try finding the type by prefixing it with the current
    //    context and all baseclasses of the current context
    if (!type && !TypeDatabase::instance()->isClassRejected(qualified_name) && m_current_class != 0 && resolveScope) {
        QStringList contexts;
        contexts.append(m_current_class->qualifiedCppName());
        contexts.append(currentScope()->qualifiedName().join("::"));

        // qDebug() << "9."<< contexts;

        TypeInfo info = typei;
        bool subclasses_done = false;
        while (!contexts.isEmpty() && type == 0) {
            //type = TypeDatabase::instance()->findType(contexts.at(0) + "::" + qualified_name);

            bool ok;
            info.setQualifiedName(QStringList() << contexts.at(0) << qualified_name);
            //   qDebug()<< "whiling in 9. type," << info.toString();
            AbstractMetaType *t = translateType(info, &ok, contextString, true, false);
            if (t != 0 && ok) {
                //     qDebug()<<"Returning ok from t != 0 && ok"<<t->fullName();
                return t;
            }

            //13. Try if the type is in a base class
            //e.g. MimeType is QWebPluginFactory::MimeType and called by QWebPluginFactory::Plugin
            QString base = contexts.at(0);
            QStringList parts = base.split("::");
            while (parts.size() > 1) {
                parts.removeLast();
                info.setQualifiedName(QStringList() << parts << qualified_name);
                AbstractMetaType *t = translateType(info, &ok, contextString, true, false);
                if (t != 0 && ok) {
                    //       qDebug()<<"Returning ok from 11. method"<<t->fullName();
                    return t;
                }
            }

            ClassModelItem item = m_dom->findClass(contexts.at(0));
            if (item != 0) {
                contexts += item->baseClasses();
            }
            contexts.removeFirst();

            // 14. Last resort: special cased prefix of Qt namespace since the meta object implicitly inherits this, so
            //     enum types from there may be addressed without any scope resolution in properties.
            if (contexts.size() == 0 && !subclasses_done) {
                contexts << "Qt";
                subclasses_done = true;
            }
        }
    }

    // if error happened in type resolving
    if (!type) {
        *ok = false;
        //   qDebug()<<"Every type checking methods failed, typei:"<<typei.toString()<<", type_info:"<<type_info.toString();
        return 0;
    }

    //qDebug()<<"A type found:"<<type->name();

    // Used to for diagnostics later
    m_used_types << type;

    // These are only implicit and should not appear in code
    Q_ASSERT(!type->isInterface());

    AbstractMetaType *meta_type = createMetaType();
    meta_type->setTypeEntry(type);
    meta_type->setIndirections(typeInfo.indirections);
    meta_type->setReference(typeInfo.is_reference);
    meta_type->setConstant(typeInfo.is_constant);
    meta_type->setOriginalTypeDescription(type_info.toString());
    decideUsagePattern(meta_type, contextString);

    if (meta_type->typeEntry()->isContainer()) {
        //   qDebug()<<"The type is a container, descending...";
        ContainerTypeEntry::Type container_type = static_cast<const ContainerTypeEntry *>(type)->type();

        if (container_type == ContainerTypeEntry::StringListContainer) {
            TypeInfo info;
            info.setQualifiedName(QStringList() << "QString");
            AbstractMetaType *targ_type = translateType(info, ok, contextString);

            Q_ASSERT(*ok);
            Q_ASSERT(targ_type);

            meta_type->addInstantiation(targ_type);
            meta_type->setInstantiationInCpp(false);

        } else if (container_type == ContainerTypeEntry::QVector2DArrayContainer) {
            TypeInfo info;
            info.setQualifiedName(QStringList() << "QVector2D");
            AbstractMetaType *targ_type = translateType(info, ok);

            Q_ASSERT(*ok);
            Q_ASSERT(targ_type);

            meta_type->addInstantiation(targ_type);
            meta_type->setInstantiationInCpp(false);

        } else if (container_type == ContainerTypeEntry::QVector3DArrayContainer) {
            TypeInfo info;
            info.setQualifiedName(QStringList() << "QVector3D");
            AbstractMetaType *targ_type = translateType(info, ok);

            Q_ASSERT(*ok);
            Q_ASSERT(targ_type);

            meta_type->addInstantiation(targ_type);
            meta_type->setInstantiationInCpp(false);

        } else if (container_type == ContainerTypeEntry::QVector4DArrayContainer) {
            TypeInfo info;
            info.setQualifiedName(QStringList() << "QVector4D");
            AbstractMetaType *targ_type = translateType(info, ok);

            Q_ASSERT(*ok);
            Q_ASSERT(targ_type);

            meta_type->addInstantiation(targ_type);
            meta_type->setInstantiationInCpp(false);

        } else {
            foreach(const TypeParser::Info &ta, typeInfo.template_instantiations) {
                TypeInfo info;
                info.setConstant(ta.is_constant);
                info.setReference(ta.is_reference);
                info.setIndirections(ta.indirections);

                info.setFunctionPointer(false);
                QString qualifiedName = ta.instantiationName();
                if (container_type == ContainerTypeEntry::QQmlListPropertyContainer || container_type == ContainerTypeEntry::QDeclarativeListPropertyContainer) {
                    QList<bool> ic = info.indirections();
                    if(!ic.isEmpty())ic.removeAt(0);
                    info.setIndirections(ic);
                }
                info.setQualifiedName(qualifiedName.split("::"));

                //    qDebug()<< "Foreaching in container thingy," << info.toString();
                AbstractMetaType *targ_type = translateType(info, ok, contextString);
                if (!(*ok)) {
                    delete meta_type;
                    return 0;
                }
                if (container_type == ContainerTypeEntry::QQmlListPropertyContainer || container_type == ContainerTypeEntry::QDeclarativeListPropertyContainer) {
                    QList<bool> ic = targ_type->indirections();
                    if(!ic.isEmpty())ic.removeAt(0);
                    targ_type->setIndirections(ic);
                }
                meta_type->addInstantiation(targ_type);
            }
        }

        if (container_type == ContainerTypeEntry::ListContainer
                || container_type == ContainerTypeEntry::VectorContainer
                || container_type == ContainerTypeEntry::StringListContainer
                || container_type == ContainerTypeEntry::QVector2DArrayContainer
                || container_type == ContainerTypeEntry::QVector3DArrayContainer
                || container_type == ContainerTypeEntry::QVector4DArrayContainer) {
            Q_ASSERT(meta_type->instantiations().size() == 1);
        }else
            // the QArray type can have two template arguments
            if (container_type == ContainerTypeEntry::QArrayContainer) {
                Q_ASSERT(meta_type->instantiations().size() == 1 || meta_type->instantiations().size() == 2);
        }
    }

    return meta_type;
}

void AbstractMetaBuilder::decideUsagePattern(AbstractMetaType *meta_type, const QString &contextString) {
    const TypeEntry *type = meta_type->typeEntry();

    if (type->isPrimitive() && (meta_type->actualIndirections() == 0
                                || (meta_type->isConstant() && meta_type->isReference() && meta_type->indirections().size() == 0))) {
        meta_type->setTypeUsagePattern(AbstractMetaType::PrimitivePattern);

    } else if (type->isVoid()) {
        meta_type->setTypeUsagePattern(AbstractMetaType::NativePointerPattern);

    } else if (type->isStringRef()
               && meta_type->indirections().size() == 0
               && (meta_type->isConstant() == meta_type->isReference()
                   || meta_type->isConstant())) {
        meta_type->setTypeUsagePattern(AbstractMetaType::StringRefPattern);

    } else if (type->isString()
               && meta_type->indirections().size() == 0
               && (meta_type->isConstant() == meta_type->isReference()
                   || meta_type->isConstant())) {
        meta_type->setTypeUsagePattern(AbstractMetaType::StringPattern);

    } else if (type->isChar()
               && meta_type->indirections().size() == 0
               && meta_type->isConstant() == meta_type->isReference()) {
        meta_type->setTypeUsagePattern(AbstractMetaType::CharPattern);

    } else if (type->isJObjectWrapper()
               && meta_type->indirections().size() == 0
               && meta_type->isConstant() == meta_type->isReference()) {
        meta_type->setTypeUsagePattern(AbstractMetaType::JObjectWrapperPattern);

    } else if (type->isVariant()
               && meta_type->indirections().size() == 0
               && meta_type->isConstant() == meta_type->isReference()) {
        meta_type->setTypeUsagePattern(AbstractMetaType::VariantPattern);

    } else if (type->isEnum() && meta_type->actualIndirections() == 0) {
        meta_type->setTypeUsagePattern(AbstractMetaType::EnumPattern);

    } else if (type->isObject()
               && meta_type->indirections().size() == 0
               && meta_type->isReference()) {
        if (((ComplexTypeEntry *) type)->isQObject())
            meta_type->setTypeUsagePattern(AbstractMetaType::QObjectPattern);
        else
            meta_type->setTypeUsagePattern(AbstractMetaType::ObjectPattern);

    } else if (type->isObject()
               && meta_type->indirections().size() == 1) {
        if (((ComplexTypeEntry *) type)->isQObject())
            meta_type->setTypeUsagePattern(AbstractMetaType::QObjectPattern);
        else
            meta_type->setTypeUsagePattern(AbstractMetaType::ObjectPattern);

        // const-references to pointers can be passed as pointers
        if (meta_type->isReference() && meta_type->isConstant()) {
            meta_type->setReference(false);
            meta_type->setConstant(false);
        }

    } else if (type->isContainer() && meta_type->indirections().size() == 0) {
        meta_type->setTypeUsagePattern(AbstractMetaType::ContainerPattern);

    } else if (type->isTemplateArgument()) {

    } else if (type->isFlags()
               && meta_type->indirections().size() == 0
               && (meta_type->isConstant() == meta_type->isReference())) {
        meta_type->setTypeUsagePattern(AbstractMetaType::FlagsPattern);

    } else if (type->isArray()) {
        meta_type->setTypeUsagePattern(AbstractMetaType::ArrayPattern);

    } else if (type->isThread()) {
        Q_ASSERT(meta_type->indirections().size() == 1);
        meta_type->setTypeUsagePattern(AbstractMetaType::ThreadPattern);

    } else if (type->isValue()
               && meta_type->indirections().size() == 0
               && (meta_type->isConstant() == meta_type->isReference()
                   || !meta_type->isReference())) {
        meta_type->setTypeUsagePattern(AbstractMetaType::ValuePattern);

    } else if (type->isObject() && meta_type->actualIndirections() == 0) {

        ReportHandler::warning(QString("Object type '%1' passed as value. Resulting code will not compile.  %2")
                               .arg(meta_type->cppSignature())
                               .arg(contextString));
        meta_type->setTypeUsagePattern(AbstractMetaType::NativePointerPattern);

    } else {
        meta_type->setTypeUsagePattern(AbstractMetaType::NativePointerPattern);
        ReportHandler::debugFull(QString("native pointer pattern for '%1'  %2")
                                 .arg(meta_type->cppSignature())
                                 .arg(contextString));
    }
}

QString AbstractMetaBuilder::translateDefaultValue(ArgumentModelItem item, AbstractMetaType *type,
        AbstractMetaFunction *fnc, AbstractMetaClass *implementing_class,
        int argument_index) {
    QString function_name = fnc->name();
    QString class_name = implementing_class->name();

    QString replaced_expression = fnc->replacedDefaultExpression(implementing_class, argument_index + 1);
    if (fnc->removedDefaultExpression(implementing_class, argument_index + 1))
        return "";
    if (!replaced_expression.isEmpty())
        return replaced_expression;

    QString expr = item->defaultValueExpression();
    if (type->isPrimitive()) {
        if (type->name() == "boolean") {
            if (expr == "false" || expr == "true") {
                return expr;
            } else {
                bool ok = false;
                int number = expr.toInt(&ok);
                if (ok && number)
                    return "true";
                else
                    return "false";
            }
        } else if (expr == "ULONG_MAX") {
            return "Long.MAX_VALUE";
        } else if (expr == "QVariant::Invalid") {
            return QString::number(QVariant::Invalid);
        } else {
            // This can be an enum or flag so I need to delay the
            // translation untill all namespaces are completly
            // processed. This is done in figureOutEnumValues()
            return expr;
        }
    } else if (type != 0 && (type->isFlags() || type->isEnum())) {
        // Same as with enum explanation above...
        return expr;

    } else {
        // constructor or functioncall can be a bit tricky...
        if (expr == "QVariant()" || expr == "QModelIndex()") {
            return "null";
        } else if (expr == "QString()") {
            return "null";
        } else if (expr == "QStringList()") {
            return "new java.util.ArrayList<String>()";
        } else if (expr == "QStringRef()") {
            return "null";
        } else if (expr == "QChar()") {
            return "'\\0'";
        } else if ((expr.startsWith("QVector<") || expr.startsWith("QSet<") || expr.startsWith("QList<")) && expr.endsWith(">()")) {
            QString collectionType = expr.startsWith("QSet") ? "java.util.HashSet" : "java.util.ArrayList";
            QString typeParam = expr.remove(0, expr.indexOf("<")+1);
            typeParam = typeParam.left(typeParam.indexOf(">"));
            TypeEntry *typeEntry = TypeDatabase::instance()->findType(typeParam);
            if(typeEntry){
                if(typeEntry->isPrimitive()){
                    if(typeEntry->qualifiedTargetLangName()=="int"){
                        return "new " + collectionType + "<java.lang.Integer>()";
                    }else if(typeEntry->qualifiedTargetLangName()=="boolean"){
                        return "new " + collectionType + "<java.lang.Boolean>()";
                    }else if(typeEntry->qualifiedTargetLangName()=="short"){
                        return "new " + collectionType + "<java.lang.Short>()";
                    }else if(typeEntry->qualifiedTargetLangName()=="char"){
                        return "new " + collectionType + "<java.lang.Character>()";
                    }else if(typeEntry->qualifiedTargetLangName()=="byte"){
                        return "new " + collectionType + "<java.lang.Byte>()";
                    }else if(typeEntry->qualifiedTargetLangName()=="long"){
                        return "new " + collectionType + "<java.lang.Long>()";
                    }else if(typeEntry->qualifiedTargetLangName()=="double"){
                        return "new " + collectionType + "<java.lang.Double>()";
                    }else if(typeEntry->qualifiedTargetLangName()=="float"){
                        return "new " + collectionType + "<java.lang.Float>()";
                    }
                }else{
                    return "new " + collectionType + "<" + typeEntry->qualifiedTargetLangName().replace("$", ".") + ">()";
                }
            }
        } else if (expr.endsWith(")") && expr.contains("::")) {
            TypeEntry *typeEntry = TypeDatabase::instance()->findType(expr.left(expr.indexOf("::")));
            if (typeEntry)
                return typeEntry->qualifiedTargetLangName().replace("$", ".") + "." + expr.right(expr.length() - expr.indexOf("::") - 2);
        } else if (expr.endsWith(")") && type->isValue()) {
            int pos = expr.indexOf("(");

            TypeEntry *typeEntry = TypeDatabase::instance()->findType(expr.left(pos));
            if (typeEntry)
                return "new " + typeEntry->qualifiedTargetLangName().replace("$", ".") + expr.right(expr.length() - pos);
            else
                return expr;
        } else if (expr == "0") {
            return "null";
        } else if (type->isObject() || type->isValue() || expr.contains("::")) { // like Qt::black passed to a QColor
            TypeEntry *typeEntry = TypeDatabase::instance()->findType(expr.left(expr.indexOf("::")));

            expr = expr.right(expr.length() - expr.indexOf("::") - 2);
            if (typeEntry) {
                return "new " + type->typeEntry()->qualifiedTargetLangName().replace("$", ".") +
                       "(" + typeEntry->qualifiedTargetLangName().replace("$", ".") + "." + expr + ")";
            }
        }
    }

    QString warn = QString("unsupported default value '%3' of argument in function '%1', class '%2'")
                   .arg(function_name).arg(class_name).arg(item->defaultValueExpression());
    ReportHandler::warning(warn);

    return QString();
}


bool AbstractMetaBuilder::isQObject(const QString &qualified_name) {
    if (qualified_name == "QObject")
        return true;

    ClassModelItem class_item = m_dom->findClass(qualified_name);

    if (!class_item) {
        QStringList names = qualified_name.split(QLatin1String("::"));
        NamespaceModelItem ns = model_dynamic_cast<NamespaceModelItem>(m_dom);
        for (int i = 0; i < names.size() - 1 && ns; ++i)
            ns = ns->namespaceMap().value(names.at(i));
        if (ns && names.size() >= 2)
            class_item = ns->findClass(names.at(names.size() - 1));
    }

    bool isqobject = class_item && class_item->extendsClass("QObject");

    if (class_item && !isqobject) {
        QStringList baseClasses = class_item->baseClasses();
        for (int i = 0; i < baseClasses.count(); ++i) {

            isqobject = isQObject(baseClasses.at(i));
            if (isqobject)
                break;
        }
    }

    return isqobject;
}


bool AbstractMetaBuilder::isEnum(const QStringList &qualified_name) {
    CodeModelItem item = m_dom->model()->findItem(qualified_name, m_dom->toItem());
    return item && item->kind() == _EnumModelItem::__node_kind;
}

AbstractMetaType *AbstractMetaBuilder::inheritTemplateType(const QList<AbstractMetaType *> &template_types,
        AbstractMetaType *meta_type, bool *ok) {
    if (ok != 0)
        *ok = true;
    if (!meta_type || (!meta_type->typeEntry()->isTemplateArgument() && !meta_type->hasInstantiations()))
        return meta_type ? meta_type->copy() : 0;

    AbstractMetaType *returned = meta_type->copy();
    returned->setOriginalTemplateType(meta_type->copy());

    if (returned->typeEntry()->isTemplateArgument()) {
        const TemplateArgumentEntry *tae = static_cast<const TemplateArgumentEntry *>(returned->typeEntry());

        // If the template is intantiated with void we special case this as rejecting the functions that use this
        // parameter from the instantiation.
        if (template_types.size() <= tae->ordinal() || template_types.at(tae->ordinal())->typeEntry()->name() == "void") {
            if (ok != 0)
                *ok = false;
            return 0;
        }

        AbstractMetaType *t = returned->copy();
        t->setTypeEntry(template_types.at(tae->ordinal())->typeEntry());
        if(template_types.at(tae->ordinal())->indirections().size() + t->indirections().size()>0){
            t->setIndirections(QList<bool>() << false);
        }else{
            t->setIndirections(QList<bool>());
        }
        decideUsagePattern(t, "inheritTemplateType " + returned->fullName());

        delete returned;
        returned = inheritTemplateType(template_types, t, ok);
        if (ok != 0 && !(*ok))
            return 0;
    }

    if (returned->hasInstantiations()) {
        QList<AbstractMetaType *> instantiations = returned->instantiations();
        for (int i = 0; i < instantiations.count(); ++i) {
            instantiations[i] = inheritTemplateType(template_types, instantiations.at(i), ok);
            if (ok != 0 && !(*ok))
                return 0;
        }
        returned->setInstantiations(instantiations);
    }

    return returned;
}

bool AbstractMetaBuilder::inheritTemplate(AbstractMetaClass *subclass,
        const AbstractMetaClass *template_class,
        const TypeParser::Info &info) {
    QList<TypeParser::Info> targs = info.template_instantiations;

    QList<AbstractMetaType *> template_types;
    foreach(const TypeParser::Info &i, targs) {
        TypeEntry *t = TypeDatabase::instance()->findType(i.qualified_name.join("::"));

        if (t != 0) {
            AbstractMetaType *temporary_type = createMetaType();
            temporary_type->setTypeEntry(t);
            temporary_type->setConstant(i.is_constant);
            temporary_type->setReference(i.is_reference);
            temporary_type->setIndirections(i.indirections);
            template_types << temporary_type;
        }
    }

    AbstractMetaFunctionList funcs = subclass->functions();
    foreach(const AbstractMetaFunction *function, template_class->functions()) {

        if (function->isModifiedRemoved(TypeSystem::All))
            continue;

        AbstractMetaFunction *f = function->copy();
        f->setArguments(AbstractMetaArgumentList());

        bool ok = true;
        AbstractMetaType *ftype = function->type();
        f->setType(inheritTemplateType(template_types, ftype, &ok));
        if (!ok) {
            delete f;
            continue;
        }

        foreach(AbstractMetaArgument *argument, function->arguments()) {
            AbstractMetaType *atype = argument->type();

            AbstractMetaArgument *arg = argument->copy();
            arg->setType(inheritTemplateType(template_types, atype, &ok));
            if (!ok)
                break;
            f->addArgument(arg);
        }

        if (!ok) {
            delete f;
            continue ;
        }

        // There is no base class in java to inherit from here, so the
        // template instantiation is the class that implements the function..
        f->setImplementingClass(subclass);

        // We also set it as the declaring class, since the superclass is
        // supposed to disappear. This allows us to make certain function modifications
        // on the inherited functions.
        f->setDeclaringClass(subclass);


        if (f->isConstructor() && subclass->isTypeAlias()) {
            f->setName(subclass->simpleName());
        } else if (f->isConstructor()) {
            delete f;
            continue;
        }

        // if the instantiation has a function named the same as an existing
        // function we have shadowing so we need to skip it.
        bool found = false;
        for (int i = 0; i < funcs.size(); ++i) {
            if (funcs.at(i)->name() == f->name()) {
                found = true;
                continue;
            }
        }
        if (found) {
            delete f;
            continue;
        }

        ComplexTypeEntry *te = subclass->typeEntry();
        FunctionModificationList mods = function->modifications(template_class);
        for (int i = 0; i < mods.size(); ++i) {
            FunctionModification mod = mods.at(i);
            mod.signature = f->minimalSignature();

            // If we ever need it... Below is the code to do
            // substitution of the template instantation type inside
            // injected code..
#if 0
            if (mod.modifiers & Modification::CodeInjection) {
                for (int j = 0; j < template_types.size(); ++j) {
                    CodeSnip &snip = mod.snips.last();
                    QString code = snip.code();
                    code.replace(QString::fromLatin1("$$QT_TEMPLATE_%1$$").arg(j),
                                 template_types.at(j)->typeEntry()->qualifiedCppName());
                    snip.codeList.clear();
                    snip.addCode(code);
                }
            }
#endif
            te->addFunctionModification(mod);
        }

        subclass->addFunction(f);
    }

    // Clean up
    foreach(AbstractMetaType *type, template_types) {
        delete type;
    }


    {
        subclass->setTemplateBaseClass(template_class);

        subclass->setInterfaces(template_class->interfaces());
        subclass->setBaseClass(template_class->baseClass());
    }

    return true;
}

void AbstractMetaBuilder::parseQ_Property(AbstractMetaClass *meta_class, const QStringList &declarations) {
    for (int i = 0; i < declarations.size(); ++i) {
        QString p = declarations.at(i);

        /*
        Pass 1: normalize all whitespace.
        Remove leading/trailing, convert contiguous whitespace sequences to a single space character.
        */
        {
            QString newP = QString();
            const int len = p.length();
            int state = 0;
            int j;
            for (j = 0; j < len; j++) {
                QChar c = p.at(j);
                if (state == 0) { // skip leading spaces
                    if(!c.isSpace()) {
                        newP += c;
                        state++;
                    }
                } else if(state == 1) { // last token was not whitespace
                    if(c.isSpace()) { // normalize to an actual space, from maybe \r\n\v\t etc.
                        newP += ' ';
                        state++;
                    } else {
                        newP += c;
                    }
                } else { // last token was whitespace
                    if(!c.isSpace()) {
                        newP += c;
                        state--;
                    }
                }
            }

            const int newplen = newP.length();

            // remove that last space we added
            if(state >= 2 && newplen > 1 && newP.at(newplen - 1) == QChar(' ')) {
                newP = newP.left(newplen - 2);
            }

            if(!newP.isNull()) {
                if (newP != p)
                    p = newP;
            }
        }

        /*
        Pass 2: Correct the first word to always be a type (this means removing
        all whitespace from the type declaration) by looking ahead at the data.

        Convert "Q_PROPERTY(QGraphicsObject * parent READ parentObject WRITE setParentItem NOTIFY parentChanged DESIGNABLE false)"
        into "Q_PROPERTY(QGraphicsObject* parent READ parentObject WRITE setParentItem NOTIFY parentChanged DESIGNABLE false)"

        moc.exe doesn't allow any comma in the property spec string, we do just in case
        that changes someday since it can be part of a type with multiple template info.
        */
        {
            QString newFirstWord = QString();
            const int len = p.length();

            /*
            0 = start (expect isalnum() || :)
            1 = last-char-was-space (expect <*&)
            2+ = inside-open-angle (expect isalnum() || : || <>*&, || ">")
            */
            int state = 0;

            int j;
            for (j = 0; j < len; j++) {
                QChar c = p.at(j);
                if (state == 0) {
                    if (c.isLetterOrNumber() || c == QChar(':') || c == QChar('*') || c == QChar('&')) {
                        newFirstWord += c;
                    } else if(c == QChar('<')) {
                        newFirstWord += c;
                        state = 2;
                    } else if (c.isSpace()) {
                        state = 1;
                    }
                } else if (state == 1) {
                    if (c == QChar('<')) {
                        newFirstWord += c;
                        state = 2;
                    } else if (c == QChar('*') || c == QChar('&')) {
                        newFirstWord += c;
                    } else if (c.isSpace()) {
                        /*
                        Use-case from state==0: strictly speaking 2 space chars in a row are not allowed
                        Use-case from state==2: we want to eat the space between 1st and 2nd word
                        */
                    } else { // if (c.isLetterOrNumber())
                        // this is to be the 2nd word, so we are done
                        break;
                    }
                } else if (state >= 2) {
                    if (c.isLetterOrNumber() || c == QChar(':') || c == QChar('*') || c == QChar('&') || c == QChar(',')) {
                        newFirstWord += c;
                    } else if (c == QChar('<')) {
                        newFirstWord += c;
                        state++;
                    } else if (c == QChar('>')) {
                        newFirstWord += c;
                        state--;
                    } else if (c.isSpace()) {
                        // nop - strictly speaking space is only allowed around non-isalnum() chars
                    } else {
                        qDebug() << "Q_PROPERTY() parse error p=" << p;
                        newFirstWord = QString(); // abort
                        break;
                    }
                }
            }
            // cut length i from left(), stitch newFirstWord
            if(!newFirstWord.isNull()) {
                QString newP = newFirstWord + ' ' + p.mid(j);
                if (newP != p)
                    p = newP;
            }
        }

        /*
        Pass 3: Split by space character then normalize parentesis by joining
        expression parts into single elements.

        "FooClass*" "foo" "READ" "(expr" "!=" "0)" "WRITE" "setFoo"

        becomes:

        "FooClass*" "foo" "READ" "(expr != 0)" "WRITE" "setFoo"

        This is known to allow correct parsing of some Q_PROPERTY in QToolBar.
        */
        QStringList l = p.split(QLatin1String(" "));
        {
            QStringList newL = QStringList();
            const int l_size = l.size();
            int nest = 0;
            QString newItem = QString(); // recombined item
            for (int j = 0; j < l_size; j++) {
                const QString item = l.at(j); // original item
                const int item_length = item.length();
                for (int k = 0; k < item_length; k++) {
                    const QChar ch = item.at(k);
                    if (ch == QChar('('))
                        nest++;
                    else if (ch == QChar(')'))
                        nest--;
                }
                if (newItem.isNull()) {
                    newItem = item;
                } else {
                    // only add spaces between parts we join
                    newItem += ' ';
                    newItem += item;
                }
                if(nest == 0) {
                    newL.append(newItem);
                    newItem = QString(); // set to null
                }
            }
            if (!newItem.isNull())
                newL.append(newItem);

            l = newL;
        }


        QStringList qualifiedScopeName = currentScope()->qualifiedName();
        bool ok = false;
        AbstractMetaType *type = 0;
        QString scope;
        for (int j = qualifiedScopeName.size(); j >= 0; --j) {
            scope = j > 0 ? QStringList(qualifiedScopeName.mid(0, j)).join("::") + "::" : QString();
            TypeInfo info;
            info.setQualifiedName((scope + l.at(0)).split("::"));

            type = translateType(info, &ok, "parseQ_Property '" + qualifiedScopeName.join("::") + "' " + p);
            if (type != 0 && ok) {
                break;
            }
        }

        if (type == 0 || !ok) {
            ReportHandler::warning(QString("Unable to decide type of property: '%1' in class '%2'")
                                   .arg(l.at(0)).arg(meta_class->name()));
            continue;
        }

        QString typeName = scope + l.at(0);

        QPropertySpec *spec = new QPropertySpec(type->typeEntry());
        spec->setName(l.at(1));
        spec->setIndex(i);

        for (int pos = 2; pos + 1 < l.size(); pos += 2) {
            // I have seen DESIGNABLE and SCRIPTABLE examples that do not have
            //  a true/false after but another keyword.
            if (l.at(pos) == QLatin1String("READ"))
                spec->setRead(l.at(pos + 1));
            else if (l.at(pos) == QLatin1String("WRITE"))
                spec->setWrite(l.at(pos + 1));
            else if (l.at(pos) == QLatin1String("DESIGNABLE"))
                spec->setDesignable(l.at(pos + 1));
            else if (l.at(pos) == QLatin1String("SCRIPTABLE"))
                spec->setScriptable(l.at(pos + 1));
            else if (l.at(pos) == QLatin1String("RESET"))
                spec->setReset(l.at(pos + 1));
            else if (l.at(pos) == QLatin1String("NOTIFY"))
                spec->setNotify(l.at(pos + 1));
            else if (l.at(pos) == QLatin1String("USER"))
                spec->setUser(l.at(pos + 1));
            else if (l.at(pos) == QLatin1String("STORED"))
                spec->setStored(l.at(pos + 1));
            else if (l.at(pos) == QLatin1String("REVISION"))
                spec->setRevision(l.at(pos + 1));
            else
                qDebug() << "WARNING: Q_PROPERTY(" << spec->name() << ", " << typeName << "): unknown aspect " << l.at(pos);
        }

        meta_class->addPropertySpec(spec);
        delete type;
    }
}

static void hide_functions(const AbstractMetaFunctionList &l) {
    foreach(AbstractMetaFunction *f, l) {
        FunctionModification mod;
        mod.signature = f->minimalSignature();
        mod.modifiers = FunctionModification::Private;
        ((ComplexTypeEntry *) f->implementingClass()->typeEntry())->addFunctionModification(mod);
    }
}

static void remove_function(AbstractMetaFunction *f) {
    FunctionModification mod;
    mod.removal = TypeSystem::All;
    mod.signature = f->minimalSignature();
    ((ComplexTypeEntry *) f->implementingClass()->typeEntry())->addFunctionModification(mod);
}

static AbstractMetaFunctionList filter_functions(const AbstractMetaFunctionList &lst, QSet<QString> *signatures) {
    AbstractMetaFunctionList functions;
    foreach(AbstractMetaFunction *f, lst) {
        QString signature = f->minimalSignature();
        int start = signature.indexOf(QLatin1Char('(')) + 1;
        int end = signature.lastIndexOf(QLatin1Char(')'));
        signature = signature.mid(start, end - start);
        if (signatures->contains(signature)) {
            remove_function(f);
            continue;
        }
        (*signatures) << signature;
        functions << f;
    }
    return functions;
}

void AbstractMetaBuilder::setupEquals(AbstractMetaClass *cls) {
    AbstractMetaFunctionList equals;
    AbstractMetaFunctionList nequals;

    QString op_equals = QLatin1String("operator_equal");
    QString op_nequals = QLatin1String("operator_not_equal");

    AbstractMetaFunctionList functions = cls->queryFunctions(AbstractMetaClass::ClassImplements
                                         | AbstractMetaClass::NotRemovedFromTargetLang);
    foreach(AbstractMetaFunction *f, functions) {
        if (f->name() == op_equals)
            equals << f;
        else if (f->name() == op_nequals)
            nequals << f;
    }

    if (equals.size() || nequals.size()) {
        if (!cls->hasHashFunction()) {
            ReportHandler::warning(QString::fromLatin1("Class '%1' has equals operators but no qHash() function. Hashcode of objects will consistently be 0.")
                                   .arg(cls->name()));
        }

        hide_functions(equals);
        hide_functions(nequals);

        // We only need == if we have both == and !=, and one == for
        // each signature type, like QDateTime::==(QDate) and (QTime)
        // if such a thing exists...
        QSet<QString> func_signatures;
        cls->setEqualsFunctions(filter_functions(equals, &func_signatures));
        cls->setNotEqualsFunctions(filter_functions(nequals, &func_signatures));
    }
}

void AbstractMetaBuilder::setupComparable(AbstractMetaClass *cls) {
    AbstractMetaFunctionList greater;
    AbstractMetaFunctionList greaterEquals;
    AbstractMetaFunctionList less;
    AbstractMetaFunctionList lessEquals;

    QString op_greater = QLatin1String("operator_greater");
    QString op_greater_eq = QLatin1String("operator_greater_or_equal");
    QString op_less = QLatin1String("operator_less");
    QString op_less_eq = QLatin1String("operator_less_or_equal");

    AbstractMetaFunctionList functions = cls->queryFunctions(AbstractMetaClass::ClassImplements
                                         | AbstractMetaClass::NotRemovedFromTargetLang);
    foreach(AbstractMetaFunction *f, functions) {
        if (f->name() == op_greater)
            greater << f;
        else if (f->name() == op_greater_eq)
            greaterEquals << f;
        else if (f->name() == op_less)
            less << f;
        else if (f->name() == op_less_eq)
            lessEquals << f;
    }

    bool hasEquals = cls->equalsFunctions().size() || cls->notEqualsFunctions().size();

    // Conditions for comparable is:
    //     >, ==, <             - The basic case
    //     >, ==                - Less than becomes else case
    //     <, ==                - Greater than becomes else case
    //     >=, <=               - if (<= && >=) -> equal
    bool mightBeComparable = greater.size() || greaterEquals.size() || less.size() || lessEquals.size()
                             || greaterEquals.size() == 1 || lessEquals.size() == 1;

    if (mightBeComparable) {
        QSet<QString> signatures;

        // We only hide the original functions if we are able to make a compareTo() method
        bool wasComparable = false;

        // The three upper cases, prefer the <, == approach
        if (hasEquals && (greater.size() || less.size())) {
            cls->setLessThanFunctions(filter_functions(less, &signatures));
            cls->setGreaterThanFunctions(filter_functions(greater, &signatures));
            filter_functions(greaterEquals, &signatures);
            filter_functions(lessEquals, &signatures);
            wasComparable = true;
        } else if (hasEquals && (greaterEquals.size() || lessEquals.size())) {
            cls->setLessThanEqFunctions(filter_functions(lessEquals, &signatures));
            cls->setGreaterThanEqFunctions(filter_functions(greaterEquals, &signatures));
            wasComparable = true;
        } else if (greaterEquals.size() == 1 || lessEquals.size() == 1) {
            cls->setGreaterThanEqFunctions(greaterEquals);
            cls->setLessThanEqFunctions(lessEquals);
            filter_functions(less, &signatures);
            filter_functions(greater, &signatures);
            wasComparable = true;
        }

        if (wasComparable) {
            hide_functions(greater);
            hide_functions(greaterEquals);
            hide_functions(less);
            hide_functions(lessEquals);
        }
    }

}

void AbstractMetaBuilder::setupClonable(AbstractMetaClass *cls) {
    // All value types are required to have a copy constructor,
    // or they will not work as value types (it won't even compile,
    // because of calls to qRegisterMetaType(). Thus all value types
    // should be cloneable.
    if (cls->typeEntry()->isValue()) {
        cls->setHasCloneOperator(true);
        return;
    } else {
        QString op_assign = QLatin1String("operator_assign");

        AbstractMetaFunctionList functions = cls->queryFunctions(AbstractMetaClass::ClassImplements);
        foreach(AbstractMetaFunction *f, functions) {
            if ((f->name() == op_assign || f->isConstructor()) && f->isPublic()) {
                AbstractMetaArgumentList arguments = f->arguments();
                if (f->actualMinimumArgumentCount() == 1) {
                    if (cls->typeEntry()->qualifiedCppName() == arguments.at(0)->type()->typeEntry()->qualifiedCppName()) {
                        if (cls->typeEntry()->isValue()) {
                            cls->setHasCloneOperator(true);
                            return;
                        }
                    }
                }
            }
        }
    }
}

static void write_reject_log_file(const QString &name,
                                  const QMap<QString, AbstractMetaBuilder::RejectReason> &rejects) {
    QFile f(name);
    if (!f.open(QIODevice::WriteOnly | QIODevice::Text)) {
        ReportHandler::warning(QString("failed to write log file: '%1'")
                               .arg(f.fileName()));
        return;
    }

    QTextStream s(&f);


    for (int reason = 0; reason < AbstractMetaBuilder::NoReason; ++reason) {
        s << QString(72, '*') << endl;
        switch (reason) {
            case AbstractMetaBuilder::NotInTypeSystem:
                s << "Not in type system";
                break;
            case AbstractMetaBuilder::GenerationDisabled:
                s << "Generation disabled by type system";
                break;
            case AbstractMetaBuilder::RedefinedToNotClass:
                s << "Type redefined to not be a class";
                break;

            case AbstractMetaBuilder::UnmatchedReturnType:
                s << "Unmatched return type";
                break;

            case AbstractMetaBuilder::UnmatchedArgumentType:
                s << "Unmatched argument type";
                break;

            default:
                s << "unknown reason";
                break;
        }

        s << endl;

        for (QMap<QString, AbstractMetaBuilder::RejectReason>::const_iterator it = rejects.constBegin();
                it != rejects.constEnd(); ++it) {
            if (it.value() != reason)
                continue;
            s << " - " << it.key() << endl;
        }

        s << QString(72, '*') << endl << endl;
    }

}


void AbstractMetaBuilder::dumpLog() {
    {
        QString fileName("mjb_rejected_classes.log");
        QFile file(fileName);
        if (!outputDirectory().isNull())
            file.setFileName(QDir(outputDirectory()).absoluteFilePath(fileName));
        write_reject_log_file(file.fileName(), m_rejected_classes);
    }

    {
        QString fileName("mjb_rejected_enums.log");
        QFile file(fileName);
        if (!outputDirectory().isNull())
            file.setFileName(QDir(outputDirectory()).absoluteFilePath(fileName));
        write_reject_log_file(file.fileName(), m_rejected_enums);
    }

    {
        QString fileName("mjb_rejected_functions.log");
        QFile file(fileName);
        if (!outputDirectory().isNull())
            file.setFileName(QDir(outputDirectory()).absoluteFilePath(fileName));
        write_reject_log_file(file.fileName(), m_rejected_functions);
    }

    {
        QString fileName("mjb_rejected_fields.log");
        QFile file(fileName);
        if (!outputDirectory().isNull())
            file.setFileName(QDir(outputDirectory()).absoluteFilePath(fileName));
        write_reject_log_file(file.fileName(), m_rejected_fields);
    }
}

AbstractMetaClassList AbstractMetaBuilder::classesTopologicalSorted() const {
    AbstractMetaClassList res;

    AbstractMetaClassList classes = m_meta_classes;
    qSort(classes);

    QSet<AbstractMetaClass*> noDependency;
    QHash<AbstractMetaClass*, QSet<AbstractMetaClass* >* > hash;
    foreach(AbstractMetaClass *cls, classes) {
        QSet<AbstractMetaClass* > *depends = new QSet<AbstractMetaClass* >();

        if (cls->baseClass())
            depends->insert(cls->baseClass());

        foreach(AbstractMetaClass *interface, cls->interfaces()) {
            depends->insert(interface);
        }

        if (depends->empty()) {
            noDependency.insert(cls);
        } else {
            hash.insert(cls, depends);
        }
    }

    while (!noDependency.empty()) {
        foreach(AbstractMetaClass *cls, noDependency.values()) {
            if (!cls->isInterface())
                res.append(cls);
            noDependency.remove(cls);
            QHashIterator<AbstractMetaClass*, QSet<AbstractMetaClass* >* > i(hash);
            while (i.hasNext()) {
                i.next();
                i.value()->remove(cls);
                if (i.value()->empty()) {
                    AbstractMetaClass *key = i.key();
                    noDependency.insert(key);
                    hash.remove(key);
                    delete(i.value());
                }
            }
        }
    }

    if (!noDependency.empty() || !hash.empty()) {
        qWarning("dependency graph was cyclic.");
    }

    return res;
}
