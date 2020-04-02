package onyx.com.phonecloud.model;

/**
 * Created by TonyXie on 2020-03-02
 */
public class CallLogInfo {
    private int id;
    private String number;
    private int presentation;
    private long date;
    private long duration;
    private int type;
    private String subscriptionComponentName;
    private long subscriptionId;
    private int unused;
    private String name;
    private String numbertype;
    private String numberlaber;
    private String countryiso;
    private String voicemailUri;
    private String isRead;
    private String geocodedLocation;
    private String lookupUri;
    private String matchedNumber;
    private String normalizedNumber;
    private int photoId;
    private String photoUri;
    private String formattedNumber;
    private long lastModified;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getPresentation() {
        return presentation;
    }

    public void setPresentation(int presentation) {
        this.presentation = presentation;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSubscriptionComponentName() {
        return subscriptionComponentName;
    }

    public void setSubscriptionComponentName(String subscriptionComponentName) {
        this.subscriptionComponentName = subscriptionComponentName;
    }

    public long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumbertype() {
        return numbertype;
    }

    public void setNumbertype(String numbertype) {
        this.numbertype = numbertype;
    }

    public String getNumberlaber() {
        return numberlaber;
    }

    public void setNumberlaber(String numberlaber) {
        this.numberlaber = numberlaber;
    }

    public String getCountryiso() {
        return countryiso;
    }

    public void setCountryiso(String countryiso) {
        this.countryiso = countryiso;
    }

    public String getVoicemailUri() {
        return voicemailUri;
    }

    public void setVoicemailUri(String voicemailUri) {
        this.voicemailUri = voicemailUri;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getGeocodedLocation() {
        return geocodedLocation;
    }

    public void setGeocodedLocation(String geocodedLocation) {
        this.geocodedLocation = geocodedLocation;
    }

    public String getLookupUri() {
        return lookupUri;
    }

    public void setLookupUri(String lookupUri) {
        this.lookupUri = lookupUri;
    }

    public String getMatchedNumber() {
        return matchedNumber;
    }

    public void setMatchedNumber(String matchedNumber) {
        this.matchedNumber = matchedNumber;
    }

    public String getNormalizedNumber() {
        return normalizedNumber;
    }

    public void setNormalizedNumber(String normalizedNumber) {
        this.normalizedNumber = normalizedNumber;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getFormattedNumber() {
        return formattedNumber;
    }

    public void setFormattedNumber(String formattedNumber) {
        this.formattedNumber = formattedNumber;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public int getUnused() {
        return unused;
    }

    public void setUnused(int unused) {
        this.unused = unused;
    }
}
