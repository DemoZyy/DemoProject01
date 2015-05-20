package com.pubnub.api;


/**
 * Message object for GCM
 * @author Pubnub
 *
 */
public class PnGcmMessage extends PnJsonObject {

    /**
     * Constructor for PnGcmMessage
     */
    public PnGcmMessage() {
        super();
    }

    /**
     * Constructor for PnGcmMessage
     * @param json
     *         json object to be set as data for GCM message
     */
    public PnGcmMessage(PnJsonObject json) {
        super();
        setData(json);
    }

    /**
     * Set Data for PnGcmMessage
     * @param json
     *         json object to be set as data for GCM message
     */
    public void setData(PnJsonObject json) {
        try {
            this.put("data", json);
        } catch (PnJsonException e) {

        }
    }
}
