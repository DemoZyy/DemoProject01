package com.pubnub.api;


/**
 * Message object for APNS
 * @author Pubnub
 *
 */
public class PnApnsMessage extends PnJsonObject {

    /**
     * Constructor for APNS message object
     */
    public PnApnsMessage() {
        super();
    }

    private PnJsonObject getAps() {
        PnJsonObject aps = null;
        try {
            aps = (PnJsonObject) this.get("aps");
        } catch (PnJsonException e) {

        }

        if (aps == null) {
            aps = new PnJsonObject();
            try {
                this.put("aps", aps);
            } catch (PnJsonException e) {

            }
        }
        return aps;
    }
     /**
      * Set value of APS alert
      * @param alert
      *         String to be set as alert value for APNS message
      */
     public void setApsAlert(String alert) {

        try {
            PnJsonObject aps = (PnJsonObject) getAps();
            aps.put("alert", alert);
        } catch (PnJsonException e) {

        }

    }
    /**
     * Set value of APS badge
     * @param badge
     *         int to be set as badge value for APNS message
     */
    public void setApsBadge(int badge) {
        try {
            PnJsonObject aps = (PnJsonObject) (PnJsonObject) getAps();
            aps.put("badge", badge);
        } catch (PnJsonException e) {

        }

    }

    /**
     * Set value of APS sound
     * @param sound
     *         String to be set as sound value for APNS message
     */
    public void setApsSound(String sound) {

       try {
           PnJsonObject aps = (PnJsonObject) getAps();
           aps.put("sound", sound);
       } catch (PnJsonException e) {

       }

   }

}
