//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.13 at 11:55:50 AM GMT 
//


package uk.ac.ox.oucs.vidaasBilling.servlet.generated;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}daily"/>
 *         &lt;element ref="{}weekly"/>
 *         &lt;element ref="{}fortnightly"/>
 *         &lt;element ref="{}monthly"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "daily",
    "weekly",
    "fortnightly",
    "monthly"
})
@XmlRootElement(name = "backupPolicy")
public class BackupPolicy {

    @XmlElement(required = true)
    protected BigInteger daily;
    @XmlElement(required = true)
    protected BigInteger weekly;
    @XmlElement(required = true)
    protected BigInteger fortnightly;
    @XmlElement(required = true)
    protected BigInteger monthly;

    /**
     * Gets the value of the daily property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDaily() {
        return daily;
    }

    /**
     * Sets the value of the daily property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDaily(BigInteger value) {
        this.daily = value;
    }

    /**
     * Gets the value of the weekly property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getWeekly() {
        return weekly;
    }

    /**
     * Sets the value of the weekly property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setWeekly(BigInteger value) {
        this.weekly = value;
    }

    /**
     * Gets the value of the fortnightly property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFortnightly() {
        return fortnightly;
    }

    /**
     * Sets the value of the fortnightly property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFortnightly(BigInteger value) {
        this.fortnightly = value;
    }

    /**
     * Gets the value of the monthly property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMonthly() {
        return monthly;
    }

    /**
     * Sets the value of the monthly property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMonthly(BigInteger value) {
        this.monthly = value;
    }

}
