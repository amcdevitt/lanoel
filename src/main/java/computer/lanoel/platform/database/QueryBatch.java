package computer.lanoel.platform.database;

import java.util.List;

/**
 * Created by amcde on 9/24/2017.
 */
public class QueryBatch {

    private List<List<QueryParameter>> _batch;

    public void addBatch(List<QueryParameter> paramList)
    {
        _batch.add(paramList);
    }

    public List<List<QueryParameter>> getBatch()
    {
        return _batch;
    }
}
