package uk.ac.ox.oucs.vidaas.entity;

// Generated 01-Aug-2011 14:49:06 by Hibernate Tools 3.4.0.CR1

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * Logins generated by hbm2java
 */
@Entity
@Table(name = "logins", catalog = "vidaas_v3")
@NamedQueries({
    @NamedQuery(name = "Logins.findAll", query = "SELECT l FROM Logins l"),
    @NamedQuery(name = "Logins.findByUserName", query = "SELECT l FROM Logins l WHERE l.userName = :userName"),
    @NamedQuery(name = "Logins.findByPassword", query = "SELECT l FROM Logins l WHERE l.password = :password")})
public class Logins implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8755087394480845478L;
	private String userName;
	private Users users;
	private String password;

	public Logins() {
	}

	public Logins(String userName, Users users, String password) {
		this.userName = userName;
		this.users = users;
		this.password = password;
	}

	@Id
	@Column(name = "UserName", unique = true, nullable = false, length = 45)
	@NotNull
	@Length(max = 45)
	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "User_ID", nullable = false)
	@NotNull
	public Users getUsers() {
		return this.users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	@Column(name = "Password", nullable = false, length = 45)
	@NotNull
	@Length(max = 45)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}