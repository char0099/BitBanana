package app.michaelwuensch.bitbanana.transactionHistory;

import com.google.protobuf.ByteString;

public interface TransactionSelectListener {

    void onTransactionSelect(ByteString transaction, int type);
}
