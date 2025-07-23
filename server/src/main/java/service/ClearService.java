package service;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class ClearService {
    private final DataAccess dataAccess;

    public ClearService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() throws DataAccessException {
        try {
            dataAccess.clear();
        } catch (Exception e) {
            System.err.println("ClearService failed: " + e.getMessage());
            e.printStackTrace();
            throw new DataAccessException("Error: internal server error", e);
        }
    }
}
