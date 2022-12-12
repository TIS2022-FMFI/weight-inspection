public class email
{
    private int email_id;
    private String email;
    private Boolean send_exports;

    public int getEmail_id()
    {
        return email_id;
    }

    public void setEmail_id(int email_id)
    {
        this.email_id = email_id;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public Boolean getSend_exports()
    {
        return send_exports;
    }

    public void setSend_exports(Boolean send_exports)
    {
        this.send_exports = send_exports;
    }
}