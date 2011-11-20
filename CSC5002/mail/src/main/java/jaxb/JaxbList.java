package jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Used to convert a List into a XMLable list.
 * @author Baptiste Lafontaine
 * @author Julie Garonne
 *
 * @param <T> The list to convert
 */
@XmlRootElement(name="List")
public class JaxbList<T>{
    protected List<T> list;

    public JaxbList(){}

    public JaxbList(List<T> list){
        this.list=list;
    }

    @XmlElement(name="Item")
    public List<T> getList(){
        return list;
    }
}
