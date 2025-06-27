package org.example.mateproduction.util;

public enum ReportReason {
    // --- Scam or Fraudulent Activity ---
    /**
     * User is asking for money before a viewing, requesting personal information, or sending suspicious links.
     */
    PHISHING_OR_SCAM,
    /**
     * User is demanding a deposit or prepayment before any in-person meeting or contract signing.
     */
    REQUESTS_PREPAYMENT,
    /**
     * The listing appears to be for a non-existent property, or the user profile seems fake or is impersonating someone else.
     */
    FAKE_LISTING_OR_PROFILE,


    // --- Inappropriate or Harmful Content ---
    /**
     * The user is engaging in harassment, threats, or using discriminatory/hateful language.
     */
    HARASSMENT_OR_HATE_SPEECH,
    /**
     * The listing or profile contains offensive, violent, or sexually explicit photos or text.
     */
    INAPPROPRIATE_CONTENT,


    // --- Misleading or Inaccurate Information (Primarily for Ads) ---
    /**
     * Photos, description, or amenities do not accurately represent the property or person.
     */
    INACCURATE_PHOTOS_OR_DESCRIPTION,
    /**
     * The price listed is incorrect, or there are hidden fees not mentioned in the ad.
     */
    INCORRECT_PRICE_OR_TERMS,
    /**
     * The location shown on the map or in the address is significantly different from the actual location.
     */
    WRONG_LOCATION,
    /**
     * The listing is a duplicate of another ad posted by the same user.
     */
    DUPLICATE_LISTING,


    // --- General & Availability Issues ---
    /**
     * The property or room is no longer available, but the ad is still active.
     */
    LISTING_IS_UNAVAILABLE,
    /**
     * The user is posting irrelevant content, advertisements, or commercial spam.
     */
    SPAM,
    /**
     * For issues not covered by other categories.
     */
    OTHER
}
