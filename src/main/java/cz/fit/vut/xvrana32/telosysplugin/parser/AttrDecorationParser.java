package cz.fit.vut.xvrana32.telosysplugin.parser;

import com.vp.plugin.model.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.Attr;
import cz.fit.vut.xvrana32.telosysplugin.parser.declarations.*;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;
import cz.fit.vut.xvrana32.telosysplugin.utils.ParameterFactory;
import org.apache.commons.collections.Factory;

import java.util.Iterator;

public class AttrDecorationParser {
    private static final AnnoDeclaration[] annoDeclarations = {
            new AnnoCommon("AggregateRoot", Anno.AnnoType.AGGREGATE_ROOT, new ParamDeclaration[]{}),
            new AnnoCommon("AutoIncremented", Anno.AnnoType.AUTO_INCREMENTED, new ParamDeclaration[]{}),
            new AnnoCommon("DbComment", Anno.AnnoType.DB_COMMENT, new ParamDeclaration[]
                    {new ParamDeclaration("dbComment", ITaggedValueDefinition.TYPE_TEXT)}),
            new AnnoCommon("DbDefaultValue", Anno.AnnoType.DB_DEFAULT_VALUE, new ParamDeclaration[]
                    {new ParamDeclaration("dbDefaultValue", ITaggedValueDefinition.TYPE_TEXT)}),
            new AnnoCommon("DbName", Anno.AnnoType.DB_NAME, new ParamDeclaration[]
                    {new ParamDeclaration("dbName", ITaggedValueDefinition.TYPE_TEXT)}),
            new AnnoCommon("DbType", Anno.AnnoType.DB_TYPE, new ParamDeclaration[]
                    {new ParamDeclaration("dbType", ITaggedValueDefinition.TYPE_TEXT)}),
            new AnnoCommon("DefaultValue", Anno.AnnoType.DEFAULT_VALUE, new ParamDeclaration[]
                    {new ParamDeclaration("defaultValue", ITaggedValueDefinition.TYPE_TEXT)}),
            new AnnoFK("FK", Anno.AnnoType.F_K, new ParamDeclaration[]{
                    new ParamDeclaration("fkName", false),
                    new ParamDeclaration("referenced", ITaggedValueDefinition.TYPE_MODEL_ELEMENT)}),
            new AnnoCommon("InputType", Anno.AnnoType.INPUT_TYPE, new ParamDeclaration[]
                    {new ParamDeclaration("inputType", ITaggedValueDefinition.TYPE_TEXT)}),
            new AnnoCommon("Label", Anno.AnnoType.LABEL, new ParamDeclaration[]
                    {new ParamDeclaration("label", ITaggedValueDefinition.TYPE_TEXT)}),
            new AnnoCommon("LongText", Anno.AnnoType.LONG_TEXT, new ParamDeclaration[]{}),
            new AnnoCommon("Max", Anno.AnnoType.MAX, new ParamDeclaration[]
                    {new ParamDeclaration("max", ITaggedValueDefinition.TYPE_FLOATING_POINT_NUMBER)}),
            new AnnoCommon("MaxLen", Anno.AnnoType.MAX_LEN, new ParamDeclaration[]
                    {new ParamDeclaration("maxLen", ITaggedValueDefinition.TYPE_INTEGER)}),
            new AnnoCommon("Min", Anno.AnnoType.MIN, new ParamDeclaration[]
                    {new ParamDeclaration("min", ITaggedValueDefinition.TYPE_FLOATING_POINT_NUMBER)}),
            new AnnoCommon("MinLen", Anno.AnnoType.MIN_LEN, new ParamDeclaration[]
                    {new ParamDeclaration("minLen", ITaggedValueDefinition.TYPE_INTEGER)}),
            new AnnoCommon("ObjectType", Anno.AnnoType.OBJECT_TYPE, new ParamDeclaration[]{}),
            new AnnoCommon("Pattern", Anno.AnnoType.PATTERN, new ParamDeclaration[]
                    {new ParamDeclaration("pattern", ITaggedValueDefinition.TYPE_TEXT)}),
            new AnnoCommon("PrimitiveType", Anno.AnnoType.PRIMITIVE_TYPE, new ParamDeclaration[]{}),
            new AnnoCommon("Transient", Anno.AnnoType.TRANSIENT, new ParamDeclaration[]{}),
            new AnnoCommon("Unique", Anno.AnnoType.UNIQUE, new ParamDeclaration[]{}),
            new AnnoCommon("UnsignedType", Anno.AnnoType.UNSIGNED_TYPE, new ParamDeclaration[]{}),
            new AnnoCommon("Future", Anno.AnnoType.FUTURE, new ParamDeclaration[]{}),
            new AnnoCommon("NotEmpty", Anno.AnnoType.NOT_EMPTY, new ParamDeclaration[]{}),
            new AnnoCommon("NotBlank", Anno.AnnoType.NOT_BLANK, new ParamDeclaration[]{}),
            new AnnoCommon("NotNull", Anno.AnnoType.NOT_NULL, new ParamDeclaration[]{}),
            new AnnoCommon("Past", Anno.AnnoType.PAST, new ParamDeclaration[]{}),

            new AnnoGeneratedValue("GeneratedValueAUTO", Anno.AnnoType.GENERATED_VALUE, new ParamDeclaration[]{}),
            new AnnoGeneratedValue("GeneratedValueIDENTITY", Anno.AnnoType.GENERATED_VALUE, new ParamDeclaration[]{}),
            new AnnoGeneratedValue("GeneratedValueSEQUENCE", Anno.AnnoType.GENERATED_VALUE, new ParamDeclaration[]{
                    new ParamDeclaration("generatorName", ITaggedValueDefinition.TYPE_TEXT),
                    new ParamDeclaration("sequenceName", ITaggedValueDefinition.TYPE_TEXT),
                    new ParamDeclaration("allocatedSize", ITaggedValueDefinition.TYPE_TEXT)
            }),
            new AnnoGeneratedValue("GeneratedValueTABLE", Anno.AnnoType.GENERATED_VALUE, new ParamDeclaration[]{
                    new ParamDeclaration("generatorName", ITaggedValueDefinition.TYPE_TEXT),
                    new ParamDeclaration("tableName", ITaggedValueDefinition.TYPE_TEXT),
                    new ParamDeclaration("pkColumnName", ITaggedValueDefinition.TYPE_TEXT),
                    new ParamDeclaration("pkColumnValue", ITaggedValueDefinition.TYPE_TEXT),
                    new ParamDeclaration("valueColumnName", ITaggedValueDefinition.TYPE_TEXT),
                    new ParamDeclaration("allocatedSize", ITaggedValueDefinition.TYPE_TEXT)
            }),

            new AnnoSize("Size", Anno.AnnoType.SIZE, new ParamDeclaration[]{
                    new ParamDeclaration("precision", ITaggedValueDefinition.TYPE_INTEGER),
                    new ParamDeclaration("scale", ITaggedValueDefinition.TYPE_INTEGER)
            }),

            // deprecated
            new AnnoCommon("DbSize", Anno.AnnoType.DB_SIZE, new ParamDeclaration[]
                    {new ParamDeclaration("dbSize", false)}),
            new AnnoCommon("SizeMax", Anno.AnnoType.SIZE_MAX, new ParamDeclaration[]
                    {new ParamDeclaration("sizeMax", ITaggedValueDefinition.TYPE_INTEGER)}),
            new AnnoCommon("SizeMin", Anno.AnnoType.SIZE_MIN, new ParamDeclaration[]
                    {new ParamDeclaration("sizeMin", ITaggedValueDefinition.TYPE_INTEGER)}),
    };

    public static void parse(IProject vPProject, Attr attr) {
//        Iterator stereotypes;

        IClass vPClass = (IClass) vPProject.getModelElementById(attr.getParentEntity().getVpId());
        IAttribute vPAttr = (IAttribute) vPClass.getChildById(attr.getVpId());

        // check for special annotation
        if (vPAttr.isID()) {
            attr.addAnno(new Anno(Anno.AnnoType.ID));
        }

        if (vPAttr.getInitialValue() != null && !vPAttr.getInitialValue().isEmpty()) {
            Anno newAnno = new Anno(Anno.AnnoType.INITIAL_VALUE);
            newAnno.addParameter(ParameterFactory.CreateParameter(ParameterFactory.ValueType.STRING, vPAttr.getInitialValue()));
            attr.addAnno(newAnno);
        }

        // TODO constraints

        DecorationParser.checkTaggedValuesStereotype(vPAttr.getTaggedValues());
        DecorationParser.parseNonSpecialAnnosAndTags(annoDeclarations, vPAttr, attr, attr.getParentEntity());

//        stereotypes = vPAttr.stereotypeModelIterator();
//        while (stereotypes.hasNext()) {
//            IStereotype stereotype = (IStereotype) stereotypes.next();
//            if (stereotype.getName().startsWith("@")) // annotation
//            {
//                Logger.log(String.format("Found a annotation in attribute: %s, has name: %s",
//                        vPAttr.getName(),
//                        stereotype.getName()));
//
//                AnnoDeclaration annoDeclaration = getAnnoDeclarationByName(stereotype.getName().substring(1));
//                if (annoDeclaration != null) {
//                    Logger.log(String.format("Annotation declaration for %s was found",
//                            stereotype.getName()));
//                    Anno newAnno = annoDeclaration.createAnno(vPAttr,
//                            stereotype, attr.getParentEntity().getParentModel());
//                    if (newAnno != null) {
//                        Logger.log("Annotation created successfully");
//                        attr.addAnno(newAnno);
//                    }
//                }
////                Anno newAnno = evaluateAnno(vPAttr, stereotype, attr);
////                if (newAnno != null){
////                    attr.addAnno(newAnno);
////                }
//            } else if (stereotype.getName().startsWith("#")) // tags
//            {
//                attr.addTag(TagParser.parseTag(vPAttr, stereotype, attr.getParentEntity()));
////                Logger.log(String.format("Found a tag in attribute: %s, has name: %s",
////                        vPAttr.getName(),
////                        stereotype.getName()));
//            }
//        }
    }

//    private static AnnoDeclaration getAnnoDeclarationByName(String name) {
//        for (AnnoDeclaration annoDeclaration : annoDeclarations) {
//            if (annoDeclaration.name.equals(name)) {
//                return annoDeclaration;
//            }
//        }
//        return null;
//    }
}
