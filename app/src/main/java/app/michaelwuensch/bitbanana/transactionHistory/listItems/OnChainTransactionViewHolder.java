package app.michaelwuensch.bitbanana.transactionHistory.listItems;

import android.view.View;

import app.michaelwuensch.bitbanana.util.Wallet;
import app.michaelwuensch.bitbanana.R;
import app.michaelwuensch.bitbanana.contacts.ContactsManager;


public class OnChainTransactionViewHolder extends TransactionViewHolder {

    private OnChainTransactionItem mOnChainTransactionItem;

    public OnChainTransactionViewHolder(View v) {
        super(v);
    }

    public void bindOnChainTransactionItem(OnChainTransactionItem onChainTransactionItem) {
        mOnChainTransactionItem = onChainTransactionItem;

        // Standard state. This prevents list entries to get mixed states because of recycling of the ViewHolder.
        setDisplayMode(true);

        if (onChainTransactionItem.getOnChainTransaction().getNumConfirmations() == 0) {
            setDisplayMode(false);
        }

        // Get amounts
        Long amount = onChainTransactionItem.getOnChainTransaction().getAmount();
        long fee = onChainTransactionItem.getOnChainTransaction().getTotalFees();

        setTimeOfDay(onChainTransactionItem.mCreationDate);

        // is internal?
        if (Wallet.getInstance().isTransactionInternal(onChainTransactionItem.getOnChainTransaction())) {

            setIcon(TransactionIcon.INTERNAL);
            setFee(fee, false);

            switch (amount.compareTo(0L)) {
                case 0:
                    // amount = 0
                    setAmount(amount, false);
                    setPrimaryDescription(mContext.getString(R.string.force_closed_channel));
                    String aliasForceClose = Wallet.getInstance().getNodeAliasFromChannelTransaction(onChainTransactionItem.getOnChainTransaction(), mContext);
                    setSecondaryDescription(aliasForceClose, true);
                    break;
                case 1:
                    // amount > 0 (Channel closed)
                    setAmount(amount, false);
                    setPrimaryDescription(mContext.getString(R.string.closed_channel));
                    String aliasClosed = Wallet.getInstance().getNodeAliasFromChannelTransaction(onChainTransactionItem.getOnChainTransaction(), mContext);
                    setSecondaryDescription(aliasClosed, true);
                    break;
                case -1:
                    // amount < 0 (Channel opened)
                    // Here we use the fee for the amount, as this is what we actually have to pay.
                    // Doing it this way looks nicer than having 0 for amount and the fee in small.
                    setAmount(fee * -1, true);
                    setPrimaryDescription(mContext.getString(R.string.opened_channel));
                    String aliasOpened;
                    if (ContactsManager.getInstance().doesContactDataExist(Wallet.getInstance().getNodePubKeyFromChannelTransaction(onChainTransactionItem.getOnChainTransaction()))) {
                        aliasOpened = ContactsManager.getInstance().getNameByContactData(Wallet.getInstance().getNodePubKeyFromChannelTransaction(onChainTransactionItem.getOnChainTransaction()));
                    } else {
                        aliasOpened = Wallet.getInstance().getNodeAliasFromChannelTransaction(onChainTransactionItem.getOnChainTransaction(), mContext);
                    }
                    setSecondaryDescription(aliasOpened, true);
                    break;
            }
        } else {
            // It is a normal transaction
            setIcon(TransactionIcon.ONCHAIN);
            setAmount(amount, true);
            setSecondaryDescription("", false);

            switch (amount.compareTo(0L)) {
                case 0:
                    // amount = 0 (should actually not happen)
                    setFee(fee, false);
                    setPrimaryDescription(mContext.getString(R.string.internal));
                    break;
                case 1:
                    // amount > 0 (received on-chain)
                    setFee(fee, false);
                    setPrimaryDescription(mContext.getString(R.string.received));
                    break;
                case -1:
                    // amount < 0 (sent on-chain)
                    setFee(fee, true);
                    setPrimaryDescription(mContext.getString(R.string.sent));
                    break;
            }
        }

        // Set on click listener
        setOnRootViewClickListener(onChainTransactionItem, HistoryListItem.TYPE_ON_CHAIN_TRANSACTION);
    }

    @Override
    public void refreshViewHolder() {
        bindOnChainTransactionItem(mOnChainTransactionItem);
        super.refreshViewHolder();
    }
}