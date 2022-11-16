package it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility;

public final class RoutesUtility {

    public static final String API_VERSION = "v1";

    public static final String API_PROCESS = "process";
    public static final String API_TRANSACTIONS = "transactions";

    public static final String API_PATH_TYPE_VAR = "type";

    public static final String API_QP_TIMESTAMP = "timestamp";
    public static final String API_QP_PAGE = "page";
    public static final String API_QP_LIMIT= "limit";


    public static final String API_TYPE_EXTS = "/{" + API_PATH_TYPE_VAR + "}";

    public static final String API_PROCESS_PATH = "/" + API_VERSION + "/" + API_PROCESS;
    public static final String API_TRANSACTIONS_PATH = "/" + API_VERSION + "/" + API_TRANSACTIONS + API_TYPE_EXTS;

    public static final String API_PROCESSOR_TAG = "Data Processor";
    public static final String API_TRANSACTIONS_TAG = "Transactions";

}