package com.artillexstudios.axapi.libraries;

public class Relocation {
    private final String from;
    private final String to;

    public Relocation(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String from() {
        return this.from;
    }

    public String to() {
        return this.to;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Relocation)) {
            return false;
        }
        Relocation that = (Relocation) object;
        return java.util.Objects.equals(this.from, that.from) && java.util.Objects.equals(this.to, that.to);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(from, to);
    }


    @Override
    public String toString() {
        return "Relocation[from=" + this.from + ", to=" + this.to + "]";
    }

}
