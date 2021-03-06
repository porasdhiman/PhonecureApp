package com.worksdelight.phonecure;

/**
 * Created by worksdelight on 15/03/17.
 */

public class GlobalConstant {
    public static String PREF_NAME = "PhoneCure";
    public static String USERID = "user_id";
    public static String FORGOT_URL = "http://m.phonecure.eu/index.php/api/users/forgetpassword";
    public static String TWITTER_URL = "http://m.phonecure.eu/index.php/api/users/twitterlogin";
    public static String FACEBOOK_URL = "http://m.phonecure.eu/index.php/api/users/facebooklogin";
    public static String FACEBOOK_REGISTER_URL = "http://m.phonecure.eu/index.php/api/users/facebookSignup";

    

    public static String SIGNUP_URL = "http://m.phonecure.eu/index.php/api/users/register";
    public static String TECHSIGNUP_URL = "http://m.phonecure.eu/index.php/api/users/register";
    public static String CATEGORY_URL = "http://m.phonecure.eu/index.php/api/categories/all";
    public static String SUB_CATEGORY_URL = "http://m.phonecure.eu/index.php/api/deviceModelServices/all?device_model_id=";
    public static String LOGIN_URL = "http://m.phonecure.eu/index.php/api/users/login";
    public static String IMAGE_URL = "http://m.phonecure.eu/uploads/categories/";
    public static String DEVICE_URL = "http://m.phonecure.eu/index.php/api/devicemodels/all?";
    public static String DEVICE_IMAGE_URL = "http://m.phonecure.eu/uploads/device_models/";
    public static String SEARCH_URL = "http://m.phonecure.eu/index.php/api/users/search";
    public static String SUB_CAETGORY_IMAGE_URL = "http://m.phonecure.eu/uploads/services/";
    public static String TECHNICIANS_IMAGE_URL = "http://m.phonecure.eu/uploads/users/";
    public static String COLOR_IMAGE_URL = "http://m.phonecure.eu/uploads/colors/";
    public static String BOOKING_URL = "http://m.phonecure.eu/index.php/api/bookings/add";
    public static String ACCORDING_TO_NAME_URL= "http://m.phonecure.eu/index.php/api/devicemodels/onewithslug?slug=";

    public static String PROFILE_URL= "http://m.phonecure.eu/index.php/api/users/profile";
    public static String CHANGEPASSWORD_URL= "http://m.phonecure.eu/index.php/api/users/changePassword";
    public static String TECH_IMAGE_URL= "http://m.phonecure.eu/uploads/users/";
    public static String COM_AND_DEL_URL= "http://m.phonecure.eu/index.php/api/bookings/action";
    public static String SERVICE_ENABLE_URL= "http://m.phonecure.eu/index.php/api/technicianServices/action";

    public static String NOTIFY_URL= "http://m.phonecure.eu/index.php/api/systemNotifications/all?user_id=";

    public static String DEVICE_GUIDE_URL= "https://www.ifixit.com/api/2.0/categories";

    public static String SERVICE_GUIDE_URL= "https://www.ifixit.com/api/2.0/categories/";
    public static String SEARCH_SERVICE_GUIDE_URL= "https://www.ifixit.com/api/2.0/search/";
    public static String email = "email";
    public static String password = "password";
    public static String name = "name";
    public static String type = "type";
    public static String device_token = "device_token";
    public static String latitude = "latitude";
    public static String longitude = "longitude";
    public static String device_type = "device_type";
    public static String facebook_id = "facebook_id";
    public static String twitter_id = "twitter_id";
    public static String id = "id";
    public static String category_id = "category_id";
    public static String icon = "icon";


    public static String device_model_id = "device_model_id";
    public static String dm_sub_category_ids = "dm_sub_category_ids";
    public static String sub_category_id="sub_category_id";
    public static String availability="availability";
    public static String off_days="off_days";
    public static String distance="distance";
    public static String favorite="favorite";
    public static String rating="rating";
    public static String average_rating="average_rating";
    public static String count="count";
    public static String image="image";
    public static String dm_sub_category_id="dm_service_id";
    public static String price="price";
    public static String service="service";
    public static String opening_time="opening_time";
    public static String closing_time="closing_time";
    public static String STARTING_URL="http://m.phonecure.eu/index.php/api/technicians/welcome?";


    public static String FAVORITE_URL="http://m.phonecure.eu/index.php/api/users/getFavoriteTechnicians?";

    public static String VAT_INFO_URL="http://apilayer.net/api/validate?access_key=fe47111e1c0d975bb965f58f1c975d9d&vat_number=";

    public static String WISHLIST_URL="http://m.phonecure.eu/index.php/api/wishlist/add";

    public static String favorite_user_id="favorite_user_id";
    public static String SERVICEADD_URL="http://m.phonecure.eu/index.php/api/technicianServices/add";
    public static String BRANDNAME_URL="http://m.phonecure.eu/index.php/api/subcategories/all?";

    public static String BOOKINGINFO_URL="http://m.phonecure.eu/index.php/api/bookings/get?";
    public static String PDF_DOWNLOAD_URL="http://m.phonecure.eu/uploads/invoices/";
    public static String IF_RATING_PENDING="http://m.phonecure.eu/index.php/api/wishlist/ifRatingPending?user_id=";

    public static String technician_id="technician_id";
    public static String firstname="firstname";
    public static String lastname="lastname";
    public static String address="address";
    public static String city="city";
    public static String zip="zip";
    public static String phone="phone";
    public static String date="date";
    public static String time="time";
    public static String total_amount="total_amount";
    public static String payment_method_nonce="payment_method_nonce";
    public static String booking_items="booking_items";
    public static String service_id="service_id";
    public static String quantity="quantity";
    public static String color="color";
    public static String COLORGET_URL="http://m.phonecure.eu/index.php/api/devicemodels/getColorsWithSlug";
    public static String ship_firstname="ship_firstname";
    public static String ship_lastname="ship_lastname";
    public static String ship_address="ship_address";
    public static String ship_city="ship_city";
    public static String ship_zip="ship_zip";
    public static String ship_phone="ship_phone";
    public static String old_password="old_password";
    public static String new_password="new_password";
    public static String confirm_password="confirm_password";
    public static String vat_number="vat_number";
    public static String organization="organization";
    public static String color_images="color_images";
    public static String model_image="model_image";
    public static String color_image="color_image";
    public static String color_id="color_id";
    public static String value="value";
    public static String sub_category="sub_category";
    public static String sub_categories="sub_categories";
    public static String status="status";
    public static String services_count="services_count";
    public static String services="services";
    public static String available_colors="available_colors";
    public static String expected_time="expected_time";
    public static String color_name="color_name";
    public static String device_model_colors="device_model_colors";
    public static String completed="completed";
    public static String pending="pending";
    public static String user_detail="user_detail";
    public static String pick_up="pick_up";
    public static String drop_off="drop_off";
    public static String technician_detail="technician_detail";
    public static String technician_service_id="technician_service_id";
    public static String enabled_status="enabled_status";
    public static String day="day";
    public static String repair_at_shop="repair_at_shop";
    public static String repair_on_location="repair_on_location";
    public static String total_bookings="total_bookings";
    public static String reviews="reviews";
    public static String invoice="invoice";
    public static String notification="notification";
    public static String RATING_URL="http://m.phonecure.eu/index.php/api/wishlist/rating";
    public static String selected_color_id="selected_color_id";
    public static String estimated_travel_time="estimated_travel_time";
    public static String address_latitude="address_latitude";
    public static String address_longitude="address_longitude";
    public static String other_charges="other_charges";
    public static String favorite_count="favorite_count";
    public static String payment_gateway="payment_gateway";
    public static String payment_token="payment_token";


    public static String number_of_employees="number_of_employees";
    public static String average_rating_count="average_rating_count";
    public static String GET_TIME_SLOT_URL="http://m.phonecure.eu/index.php/api/technicians/timeslots?";
    public static String start="start";
    public static String Demo_booking_url="http://m.phonecure.eu/index.php/api/bookings/generate";


    public static String vat="vat";
    public static String GetCouponDetail="http://m.phonecure.eu/index.php/api/discounts/getone?code=";
    public static String cancelCoupon="http://m.phonecure.eu/index.php/api/discounts/cancel";
    public static String applyCoupon="http://m.phonecure.eu/index.php/api/discounts/apply";

    public static String discount_coupon_id="discount_coupon_id";
    public static String sub_total="sub_total";
    public static String discounted_sub_total="discounted_sub_total";
    public static String discount="discount";
    public static String user_discount_coupon_id="user_discount_coupon_id";
    public static String vat_percentage="vat_percentage";
    public static String vat_value="vat_value";
}
