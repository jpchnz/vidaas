package uk.ac.ox.oucs.vidaas.entity;

// Generated 01-Aug-2011 14:49:06 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * DatabaseStructure generated by hbm2java
 */
@Entity
@Table(name = "database_structure", catalog = "vidaas_v3")
@NamedQueries({
    @NamedQuery(name = "DatabaseStructure.findAll", query = "SELECT d FROM DatabaseStructure d"),
    @NamedQuery(name = "DatabaseStructure.findByStructureID", query = "SELECT d FROM DatabaseStructure d WHERE d.structureId = :structureID"),
    @NamedQuery(name = "DatabaseStructure.findByContentType", query = "SELECT d FROM DatabaseStructure d WHERE d.contentType = :contentType"),
    @NamedQuery(name = "DatabaseStructure.findByCreationDate", query = "SELECT d FROM DatabaseStructure d WHERE d.creationDate = :creationDate"),
    @NamedQuery(name = "DatabaseStructure.findByCSVDirectory", query = "SELECT d FROM DatabaseStructure d WHERE d.csvDirectory = :cSVDirectory"),
    @NamedQuery(name = "DatabaseStructure.findByDatabaseDirectory", query = "SELECT d FROM DatabaseStructure d WHERE d.databaseDirectory = :databaseDirectory"),
    @NamedQuery(name = "DatabaseStructure.findByFile", query = "SELECT d FROM DatabaseStructure d WHERE d.file = :file"),
    @NamedQuery(name = "DatabaseStructure.findBySize", query = "SELECT d FROM DatabaseStructure d WHERE d.size = :size"),
    @NamedQuery(name = "DatabaseStructure.findBySQLDirectory", query = "SELECT d FROM DatabaseStructure d WHERE d.sqlDirectory = :sQLDirectory"),
    @NamedQuery(name = "DatabaseStructure.findByStatus", query = "SELECT d FROM DatabaseStructure d WHERE d.status = :status"),
    @NamedQuery(name = "DatabaseStructure.findBySchemaType", query = "SELECT d FROM DatabaseStructure d WHERE d.schemaType = :schemaType"),
    @NamedQuery(name = "DatabaseStructure.findByUploadType", query = "SELECT d FROM DatabaseStructure d WHERE d.uploadType = :uploadType")})
public class DatabaseStructure implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4662294537068732341L;
	private Integer structureId;
	private String contentType;
	private Date creationDate;
	private String csvDirectory;
	private byte[] data;
	private String databaseDirectory;
	private String file;
	private long size;
	private String sqlDirectory;
	private String status;
	private String schemaType;
	private String uploadType;
	private Set<ProjectDatabase> projectDatabases = new HashSet<ProjectDatabase>(
			0);
	private Set<SchemaLog> schemaLogs = new HashSet<SchemaLog>(0);

	public DatabaseStructure() {
	}

	public DatabaseStructure(Date creationDate, String csvDirectory,
			String databaseDirectory, String file, String sqlDirectory,
			String schemaType) {
		this.creationDate = creationDate;
		this.csvDirectory = csvDirectory;
		this.databaseDirectory = databaseDirectory;
		this.file = file;
		this.sqlDirectory = sqlDirectory;
		this.schemaType = schemaType;
	}

	public DatabaseStructure(String contentType, Date creationDate,
			String csvDirectory, byte[] data, String databaseDirectory,
			String file, long size, String sqlDirectory, String status,
			String schemaType, String uploadType,
			Set<ProjectDatabase> projectDatabases, Set<SchemaLog> schemaLogs) {
		this.contentType = contentType;
		this.creationDate = creationDate;
		this.csvDirectory = csvDirectory;
		this.data = data;
		this.databaseDirectory = databaseDirectory;
		this.file = file;
		this.size = size;
		this.sqlDirectory = sqlDirectory;
		this.status = status;
		this.schemaType = schemaType;
		this.uploadType = uploadType;
		this.projectDatabases = projectDatabases;
		this.schemaLogs = schemaLogs;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "Structure_ID", unique = true, nullable = false)
	public Integer getStructureId() {
		return this.structureId;
	}

	public void setStructureId(Integer structureId) {
		this.structureId = structureId;
	}

	@Column(name = "Content_Type")
	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "Creation_Date", nullable = false, length = 10)
	@NotNull
	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Column(name = "CSV_Directory", nullable = false, length = 145)
	@NotNull
	@Length(max = 145)
	public String getCsvDirectory() {
		return this.csvDirectory;
	}

	public void setCsvDirectory(String csvDirectory) {
		this.csvDirectory = csvDirectory;
	}

	@Column(name = "Data")
	public byte[] getData() {
		return this.data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	@Column(name = "Database_Directory", nullable = false, length = 145)
	@NotNull
	@Length(max = 145)
	public String getDatabaseDirectory() {
		return this.databaseDirectory;
	}

	public void setDatabaseDirectory(String databaseDirectory) {
		this.databaseDirectory = databaseDirectory;
	}

	@Column(name = "File", nullable = false, length = 145)
	@NotNull
	@Length(max = 145)
	public String getFile() {
		return this.file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	@Column(name = "Size")
	public long getSize() {
		return this.size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@Column(name = "SQL_Directory", nullable = false, length = 145)
	@NotNull
	@Length(max = 145)
	public String getSqlDirectory() {
		return this.sqlDirectory;
	}

	public void setSqlDirectory(String sqlDirectory) {
		this.sqlDirectory = sqlDirectory;
	}

	@Column(name = "Status")
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "Schema_Type", nullable = false, length = 45)
	@NotNull
	@Length(max = 45)
	public String getSchemaType() {
		return this.schemaType;
	}

	public void setSchemaType(String schemaType) {
		this.schemaType = schemaType;
	}

	@Column(name = "Upload_Type")
	public String getUploadType() {
		return this.uploadType;
	}

	public void setUploadType(String uploadType) {
		this.uploadType = uploadType;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "databaseStructure")
	public Set<ProjectDatabase> getProjectDatabases() {
		return this.projectDatabases;
	}

	public void setProjectDatabases(Set<ProjectDatabase> projectDatabases) {
		this.projectDatabases = projectDatabases;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "databaseStructure")
	public Set<SchemaLog> getSchemaLogs() {
		return this.schemaLogs;
	}

	public void setSchemaLogs(Set<SchemaLog> schemaLogs) {
		this.schemaLogs = schemaLogs;
	}

}