/**
 * Created by Kristoffer on 2015-09-29.
 */
public class User {

    private String name;
    private String[] interests;



    @Override
    public boolean equals(Object o){
        if(o == null) return false;
        if(o == this) return true;
        if(!(o instanceof User)) return false;

        User user = (User)o;

        return (user.name.equalsIgnoreCase(this.name));
    }


    @Override
    public int hashCode(){
        return name.toLowerCase().hashCode()*17*5*23;
    }
}
