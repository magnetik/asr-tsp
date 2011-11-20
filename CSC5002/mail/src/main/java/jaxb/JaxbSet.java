package jaxb;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Used to convert a Set into a XMLable Set.
 * @author Baptiste Lafontaine
 * @author Julie Garonne
 *
 * @param <T> The list to convert
 */
@XmlRootElement(name="List")
public class JaxbSet<T>{
    protected Set<T> set;

    public JaxbSet(){}

    public JaxbSet(Set<T> set){
        this.set=set;
    }

    @XmlElement(name="Item")
    public Set<T> getSet(){
        return set;
    }
}
