package eu.pretix.libpretixprint.templating;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FontSpecification implements Comparable<FontSpecification> {
    @Override
    public int compareTo(@NotNull FontSpecification o) {
        if (equals(o)) return 0;
        if (name.compareTo(o.name) != 0) return name.compareTo(o.name);
        return style.compareTo(o.style);
    }

    public enum Style {
        REGULAR, ITALIC, BOLD, BOLDITALIC
    }

    private Style style;
    private String name;

    public FontSpecification(String name, Style style) {
        this.style = style;
        this.name = name;
    }

    public Style getStyle() {
        return style;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FontSpecification that = (FontSpecification) o;
        return style == that.style &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(style, name);
    }
}
