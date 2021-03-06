package com.mzyupc.aredis.vo;

import com.google.common.base.Objects;
import lombok.*;

/**
 * @author mzyupc@163.com
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionInfo {

    private String id;

    private String name;

    private String url;

    private String port;

    private String password;

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConnectionInfo that = (ConnectionInfo) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
