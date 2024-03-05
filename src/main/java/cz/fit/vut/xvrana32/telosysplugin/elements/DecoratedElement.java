package cz.fit.vut.xvrana32.telosysplugin.elements;

import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared implementation of all elements that can have annotations and tags.
 * <br> <br>
 * Use these methods to manipulate with annotations / tags.
 */
public abstract class DecoratedElement extends Element {
    final List<Anno> annos = new ArrayList<>();
    final List<Tag> tags = new ArrayList<>();

    /**
     * @param _vpId ID of {@link com.vp.plugin.model.IModelElement} representing this element in VP project.
     * @param _name Name of the element.
     */
    public DecoratedElement(String _vpId, String _name) {
        super(_vpId, _name);
    }

    /**
     * Set can't contain two tags of the same name.
     *
     * @param tag Tag to add.
     * @return True if tag was added successfully, false otherwise.
     */
    public boolean addTag(Tag tag) {
        if (containsTag(tag.getTagType())) {
            return false; // tag is already in the list
        }
        return tags.add(tag);
    }

    public boolean containsTag(String tagName) {
        for (Tag tag : tags) {
            if (tag.getTagType().equals(tagName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param tagName Name of the searched tag.
     * @return Tag if found, null otherwise.
     */
    public Tag getTag(String tagName) {
        for (Tag tag : tags) {
            if (tag.getTagType().equals(tagName)) {
                return tag;
            }
        }
        return null;
    }

    /**
     * Set can not contain two annotation of the same {@link Anno.AnnoType}.
     *
     * @param anno Annotation to add.
     * @return True if annotation added successfully, false otherwise.
     */
    public boolean addAnno(Anno anno) {
        if (anno.getAnnoType() != Anno.AnnoType.F_K) {
            if (containsAnnoType(anno.getAnnoType())) {
                return false;
            }
        }

        return annos.add(anno);
    }

    public List<Anno> getAnnos() {
        return annos;
    }

    public boolean containsAnnoType(Anno.AnnoType annoType) {
        for (Anno anno : annos) {
            if (anno.getAnnoType() == annoType) {
                return true;
            }
        }
        return false;
    }

    public Anno getAnno(Anno.AnnoType annoType) {
        for (Anno anno : annos) {
            if (anno.getAnnoType() == annoType) {
                return anno;
            }
        }
        return null;
    }

    /**
     * @param separator What should be used to separate decoration in String.
     * @return Decorations in Telosys DSL format separated by separator. Not including curly brackets.
     */
    public String decorationsToString(String separator) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Anno anno : annos) {
            stringBuilder.append(anno);
            stringBuilder.append(separator);
        }
        for (Tag tag : tags) {
            stringBuilder.append(tag);
            stringBuilder.append(separator);
        }

        return stringBuilder.toString();
    }
}
