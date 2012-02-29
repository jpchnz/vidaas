package uk.ac.ox.oucs.vidaas.entity;

// Generated 01-Aug-2011 14:49:06 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import org.hibernate.validator.Length;

/**
 * WebApplication generated by hbm2java
 */
@Entity
@Table(name = "web_application", catalog = "vidaas_v3")
@NamedQueries({
    @NamedQuery(name = "WebApplication.findAll", query = "SELECT w FROM WebApplication w"),
    @NamedQuery(name = "WebApplication.findByWebID", query = "SELECT w FROM WebApplication w WHERE w.webId = :webID"),
    @NamedQuery(name = "WebApplication.findByUrl", query = "SELECT w FROM WebApplication w WHERE w.url = :url"),
    @NamedQuery(name = "WebApplication.findByVersion", query = "SELECT w FROM WebApplication w WHERE w.version = :version")})
public class WebApplication implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8331042948630374311L;
	private Integer webId;
	private Integer version;
	private String url;
	private String webApplicationName;
	private String status;
	private ProjectDatabase projectDatabase;

	public WebApplication() {
	}

	public WebApplication(String url, ProjectDatabase projectDatabase) {
		this.url = url;
		this.projectDatabase = projectDatabase;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "Web_ID", unique = true, nullable = false)
	public Integer getWebId() {
		return this.webId;
	}

	public void setWebId(Integer webId) {
		this.webId = webId;
	}

	@Version
	@Column(name = "Version")
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "URL", length = 145)
	@Length(max = 145)
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Column(name = "Status", length = 45)
	@Length(max = 45)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Column(name = "WebApplicationName", length = 45)
	@Length(max = 45)
	public String getWebApplicationName() {
		return webApplicationName;
	}

	public void setWebApplicationName(String webApplicationName) {
		this.webApplicationName = webApplicationName;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "webApplication")
	public ProjectDatabase getProjectDatabase() {
		return this.projectDatabase;
	}

	public void setProjectDatabase(ProjectDatabase projectDatabase) {
		this.projectDatabase = projectDatabase;
	}

}