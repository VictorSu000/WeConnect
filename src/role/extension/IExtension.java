package role.extension;

/**
 * Created by Victor on 2017/5/5.<br>
 * <br>
 * The interface which each extension must implements.
 * Each extension starts as a separate thread and has a method close() to close all the related resources.
 * In client each role will have an instance of all the extension,
 * but only when the function of the extension is to be used will the extension thread be really started.
 * So it's strongly recommended that the extension has a simple constructor, that is, just save some parameters,
 * and the complicated part (like generating GUI) is made in run() method to save computer resources.
 * Additionally, at last the client will call method close() of all the extension.
 */
public interface IExtension extends Runnable{
    @Override
    void run();
    void close();
}
