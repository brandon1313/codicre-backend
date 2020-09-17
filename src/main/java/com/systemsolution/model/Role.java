package com.systemsolution.model;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;

import com.systemsolution.user.resource.UserRoleResource;

@Entity
@Table(name = "roles")
@SqlResultSetMapping(
		name = "findByRoleUserByUserMapping",
			    classes={
			            @ConstructorResult(
			                targetClass=UserRoleResource.class,
			                columns={
			                    @ColumnResult(name="name", type=String.class),
			                    @ColumnResult(name="user_id", type=Long.class)
			                }
			            )
			        }
		)
@NamedNativeQuery(name="Role.getRoles", 
query="SELECT r.name , u.user_id FROM roles r LEFT JOIN user_roles u ON r.id = u.role_id and u.user_id= :userId",
resultSetMapping="findByRoleUserByUserMapping")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(length = 60)
    private RoleName name;

    public Role() {}

    public Role(RoleName name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }
}