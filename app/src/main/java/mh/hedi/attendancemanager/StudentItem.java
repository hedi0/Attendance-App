package mh.hedi.attendancemanager;

public class StudentItem {
    private long sid ;
    private int id ;
    private String name ;
    private String status ;

    // ki l3ada constructor
    public StudentItem(long sid , int id, String name) {
        this.sid = sid;
        this.id = id;
        this.name = name;
        status = "" ;}
    // jib l id
    public int getId(){return id;}
    // set ll id
    public void setId(int id){this.id = id;}
    // jib l nom
    public String getName(){return name;}
    // set ll nom
    public void setName(String name){this.name = name;}
    // jib l status
    public String getStatus(){return status;}
    // set ll status
    public void setStatus(String status){this.status = status;}
    public long getSid(){return sid;}

    public void setSid(long sid){this.sid = sid;}
}
