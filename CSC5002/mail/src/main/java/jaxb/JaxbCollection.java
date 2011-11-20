package jaxb;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Used to convert a Collection into a XMLable Collection.
 * @author Baptiste Lafontaine
 * @author Julie Garonne
 *
 * @param <T> The list to convert
 */
@XmlRootElement(name="Collection")
public class JaxbCollection<T>{
    protected Collection<T> Collection;

    public JaxbCollection(){}

    public JaxbCollection(Collection<T> Collection){
        this.Collection=Collection;
    }

    @XmlElement(name="Item")
    public Collection<T> getCollection(){
        return Collection;
    }
}
