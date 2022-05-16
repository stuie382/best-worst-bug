import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class BugDemo {
    Logger log = Logger.getLogger(BugDemo.class.getName());
    ContractService contractService;
    JTextField oldContractId_txt;
    JTextField migrationContractId_txt;

    /**
     * Clone an existing contract on to a new Contract ID in preparation for migration.
     *
     * @param connectionManager - The DB connection manager
     * @return - The contract details cloned to a new contract ID
     * @throws ServerException if any problems are encountered. Details are logged.
     */
    public Contract cloneToNewContractId(ConnectionManager connectionManager) throws ServerException {
        try (Connection conn = connectionManager.getConnection()) {
            String oldContractId = oldContractId_txt.getText();
            Contract oldContract = contractService.getContractById(conn, oldContractId);
            // If the old contract doesn't exist, throw the error to the user.
            if (oldContract == null) {
                throw new IllegalArgumentException("Contract ID doesn't exist.");
            }
            String migrationContractId = migrationContractId_txt.getText();
            if (migrationContractId == null) {
                throw new IllegalArgumentException("Migration Contract ID is invalid.");
            }
            Contract clonedContract = contractService.cloneExistingContractToNewId(conn,
                    oldContract,
                    migrationContractId);
            if (clonedContract == null) {
                throw new ServerException("Error cloning contract.");
            }
            return clonedContract;
        } catch (Exception ex) {
            log.severe("Problem encountered cloning contract: " + ex.getMessage());
            throw new ServerException(ex);
        }
    }
    
     public static void main(String[] args) {    }
}

   


/** STUB ENTITIES TO MAKE THE SAMPLE LOOK VALID */


/**
 * The entity representing a Contract
 */
class Contract {
}

interface ContractService {
    /**
     * Attempt to find a contract for the specified ID.
     *
     * @param connection - The DB connection
     * @param id - The ID to search for
     * @return - The specified contract entity, or null if it cannot be found.
     */
    Contract getContractById(Connection connection, String id);

    /**
     * Clone all contract details on to a new contract ID which is in the 'PENDING' state.
     *
     * @param connection - The DB connection
     * @param oldContract - The existing Contract that needs to be cloned
     * @param migrationContractId - The new Contract ID to clone against
     * @return - The cloned contract, or null if something went wrong
     */
    Contract cloneExistingContractToNewId(Connection connection, Contract oldContract, String migrationContractId);
}

interface ConnectionManager {
    /**
     * Get a managed connection to the database
     *
     * @return - The DB connection with autocommit disabled.
     */
    Connection getConnection();
}

/**
 * Custom exception that will be intercepted before it gets thrown to the GUI.
 */
class ServerException extends Exception {
    public ServerException(String message) {
    }

    public ServerException(Exception exception) {
    }
}
