package ddbb;

public class Note {

	private int id;
    private String title, content;
    
    public Note(int id,String title, String content){
    	super();
    	this.id = id;
        this.title = title;
        this.content = content;
    }
    
    public Note(int id,String title){
    	super();
    	this.id = id;
        this.title = title;
    }
    
    public int getId(){
        return this.id;
    }
    
    public void setId(int id){
        this.id = id;
    }
    public String getTitle(){
        return this.title;
    }
    // setting title
    public void setTitle(String title){
        this.title = title;
    }
    // getting content
    public String getContent(){
        return this.content;
    }
    //setting content
    public void setContent(String content){
        this.content = content;
    }
    
    @Override
    public String toString(){
    	return this.title;
    }
}