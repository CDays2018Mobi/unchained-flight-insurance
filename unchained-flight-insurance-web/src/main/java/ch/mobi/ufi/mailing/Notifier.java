package ch.mobi.ufi.mailing;

public interface Notifier {

    void notify(String from,
                String to,
                String subject,
                String content);
}