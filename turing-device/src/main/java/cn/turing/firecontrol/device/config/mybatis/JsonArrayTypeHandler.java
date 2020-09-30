package cn.turing.firecontrol.device.config.mybatis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created on 2019/03/27 10:33
 *
 * @Description TODO
 * @Version V1.0
 */
@MappedTypes({JSONArray.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonArrayTypeHandler extends BaseTypeHandler<JSONArray> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONArray parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return JSONArray.parseArray(rs.getString(columnName));
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return JSONArray.parseArray(rs.getString(columnIndex));
    }

    @Override
    public JSONArray getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return JSONArray.parseArray(cs.getString(columnIndex));
    }
}
