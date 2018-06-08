package ch.mobi.ufi.document;

public interface DocumentGenerator<C, T> {

    public T generate(C context, String templateName);
}
