import com.seeyon.v3x.dee.Document;
import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.datasource.XMLDataSource;
import com.seeyon.v3x.dee.resource.DbDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by yangyu on 2017/3/16.
 */
public class DeeClassAdapter {

    public static void main(String[] args) throws TransformException {

        boolean isBlock = false;

        final String fieldNumber = "field0003";
        final String fieldName = "field0004";
        final String fieldCount = "field0005";
        final String srcTable = "formson_0035";
        final String targetTable = "repository";
        final String dbName = "演示";
        final String sql = "select number,count from %s where number = %s and count > %d";
        final String updateSql = "update %s set count = ? where number = ?";
        final String result = "[%s] 库存超出限制！\n";
        final String error = "[%s] 的数据量必须大于0 \n";
        final String success = "库存扣减成功！";
        StringBuilder resultString = new StringBuilder();
        Connection connection = null;
        try {

            File file = new File("/Users/yangyu/Downloads/xml1.txt");
            XMLDataSource xmlDataSource = new XMLDataSource(file);
            Document formData = xmlDataSource.parse();

            DbDataSource ds = getDsByName(dbName);
            connection = ds.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(updateSql,targetTable));
            List<Document.Element> rows = formData.getRootElement().getChild(srcTable).getChildren();
            for (Document.Element row : rows){
                String number = (String) row.getChild(fieldNumber).getValue();
                String name =  (String) row.getChild(fieldName).getValue();
                int count = Integer.valueOf((String) row.getChild(fieldCount).getValue());
                if (count<=0){
                    resultString.append(String.format(error,name));
                    isBlock = true;
                    break;
                }
                String formatSql = String.format(sql,targetTable,number,count);
                ResultSet rs = connection.createStatement().executeQuery(formatSql);
                boolean isFind = rs.next();
                if (!isFind){
                    resultString.append(String.format(result,name));
                    isBlock = true;
                    break;
                }
                int repositoryCount= rs.getInt(2);
                int leftCount = repositoryCount-count;
                preparedStatement.setInt(1,leftCount);
                preparedStatement.setInt(2,Integer.valueOf(number));
                preparedStatement.addBatch();
            }

            if (isBlock){

            }else {
                preparedStatement.executeBatch();
            }

        }catch(Exception e) {
            if (connection!=null)
            {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            throw new TransformException(e);
        }finally {
            if (connection!=null){
                try {
                    connection.commit();
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }




    public static DbDataSource getDsByName(String name){
        return new DbDataSource() {
            @Override
            public Connection getConnection() throws Exception {
                return null;
            }
        };
    }

}
