package mh.hedi.attendancemanager;

public class ClassItem {
    private long cid ;

    public ClassItem(long cid, String className, String subjectName) {
        this.cid = cid;
        ClassName = className;
        SubjectName = subjectName;
    }

    private String ClassName;
    private String SubjectName;
    //Get fonction ll Class
    public String getClassName() {
        return ClassName;
    }
    //Set fonction ll Class
    public void setClassName(String className) {
        this.ClassName = className;
    }
    //Get fonction ll Subject
    public String getSubjectName() {
        return SubjectName;
    }
    //Set fonction ll Subject
    public void setSubjectName(String subjectName) {
        this.SubjectName = subjectName;
    }
    // Constructeur
    public ClassItem(String className, String subjectName) {
        this.ClassName = className;
        this.SubjectName = subjectName;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }
}
