package uk.ac.ox.oucs.vidaas.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = "xml_files", catalog = "vidaas_v3")
@NamedQueries({
	@NamedQuery(name = "XMLFiles.findByFileID", query = "SELECT p FROM XMLFiles p WHERE p.fileId = :fileId"),
	@NamedQuery(name = "XMLFiles.findByDatabaseID", query = "SELECT p FROM XMLFiles p WHERE p.projectDatabase.databaseId = :databaseID"),
	@NamedQuery(name = "XMLFiles.findByFileName", query = "SELECT p FROM XMLFiles p WHERE p.fileName = :fileName"),
	@NamedQuery(name = "XMLFiles.findByFileNameAndDatabaseID", query = "SELECT p FROM XMLFiles p WHERE p.fileName = :fileName AND p.projectDatabase.databaseId = :databaseID")
})

public class XMLFiles implements java.io.Serializable {

	
	private static final long serialVersionUID = -2439667866854494437L;
	
	private Integer fileId;
	private ProjectDatabase projectDatabase;
	private Users users;
	private String fileName;
	private long size;
	private Date uploadDate;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "File_ID", unique = true, nullable = false)
	public Integer getFileId() {
		return fileId;
	}
	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Database_ID", nullable = false)
	@NotNull
	public ProjectDatabase getProjectDatabase() {
		return this.projectDatabase;
	}

	public void setProjectDatabase(ProjectDatabase projectDatabase) {
		this.projectDatabase = projectDatabase;
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
	
	@Column(name = "File_Name", nullable = false, length = 45)
	@NotNull
	@Length(max = 45)
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@Column(name = "Size")
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "Upload_Date", nullable = false, length = 10)
	@NotNull
	public Date getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

}
