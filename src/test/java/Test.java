import com.seeyon.v3x.dee.Document;
import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.datasource.XMLDataSource;
import com.seeyon.v3x.dee.resource.DbDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangyu on 2017/3/16.
 */
public class Test {

    public static void main(String[] args) throws TransformException {

        boolean isBlock = false;
        final Map<String,String> usersMap = new HashMap();
        usersMap.put("test","-4996587393638120604");
        usersMap.put("test1","-196912475731703060");
        usersMap.put("test2","2303426100625821483");

        final String fieldNumber = "field0003";
        final String fieldName = "field0004";
        final String fieldCount = "field0005";
        final String fieldPeople = "field0006";
        final String srcTable = "formson_0035";
        final String targetTable = "repository";
        final String dbName = "演示";
        final String sql = "select 1 from %s where number = %s and count > %d";
        final String result = "[%s] 库存超出限制！\n";
        final String error = "[%s] 的数据量必须大于0 \n";
        //todo
        final String currentPeople = null;
        final String currentPeopleId = usersMap.get(currentPeople);

        StringBuilder resultString = new StringBuilder();
        Connection connection = null;
        try {

            File file = new File("/Users/yangyu/Downloads/xml1.txt");
            XMLDataSource xmlDataSource = new XMLDataSource(file);
            Document formData = xmlDataSource.parse();

            DbDataSource ds = getDsByName(dbName);
            connection = ds.getConnection();
            List<Document.Element> rows = formData.getRootElement().getChild(srcTable).getChildren();
            for (Document.Element row : rows){
                String peopleId = (String) row.getChild(fieldPeople).getValue();
                if (currentPeopleId==null||peopleId==null)
                    continue;
                if (!currentPeopleId.equals(peopleId))
                    continue;
                String number = (String) row.getChild(fieldNumber).getValue();
                String name =  (String) row.getChild(fieldName).getValue();
                int count = Integer.valueOf((String) row.getChild(fieldCount).getValue());
                if (count<=0){
                    resultString.append(String.format(error,name));
                    isBlock = true;
                    continue;
                }
                String formatSql = String.format(sql,targetTable,number,count);
                boolean isFind = connection.createStatement().executeQuery(formatSql).next();
                if (!isFind){
                    resultString.append(String.format(result,name));
                    isBlock = true;
                }
            }

            if (isBlock){

            }else {

            }

        }catch(Exception e) {
            throw new TransformException(e);
        }finally {
            if (connection!=null){
                try {
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
