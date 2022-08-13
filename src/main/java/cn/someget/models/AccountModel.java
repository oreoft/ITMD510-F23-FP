package cn.someget.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountModel {

    private int cid;
    private String uname;
    private String passwdEncrypted;
    private RoleType roleType;
    private String roleTypeString;

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
        this.roleTypeString = roleType.name();
    }
}
