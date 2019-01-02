package organizer;

/**
 * Created by Maciej on 2019-01-02 10:05
 */
public class ChoosingFormFields {
    private String type;
    private String url;
    private String name;
    private String userName;
    private String password;

    public ChoosingFormFields(){

    }

    public ChoosingFormFields(String type, String url, String name, String userName, String password) {
        this.type = type;
        this.url = url;
        this.name = name;
        this.userName = userName;
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
