package Application

import com.google.firebase.auth.FirebaseUser

// Exceptions related to User Login and Authentication
class EmailVerificationException (message: String) : Exception(message)
class UserAuthenticationException (message: String) : Exception(message) {
    override fun toString(): String {
        return "UserAuthenticationException: $message"
    }
}
class SessionAccessException (message: String) : Exception(message)
class ExistingSessionException (message: String) : Exception(message)

// Exception related to Database updates
class FirebaseDataImportException (message: String) : Exception(message)

// Exception related to Network connectivity
class NetworkConnectivityException (message: String) : Exception(message)

// Exception thrown during the Onboarding Process for errors in User Function
class UserOnboardingException (message: String) : Exception(message)

// Exception thrown during the Onboarding Process for errors in User Function
class UserCreationException (message: String) : Exception(message)

// Exception thrown during the Onboarding Process for errors in User Function
class UserEditionException (message: String) : Exception(message)

// Exception thrown during Task Creation processes
class TaskCreationException (message: String) : Exception(message)

// Exception thrown during Task Creation processes
class TaskDeletionException (message: String) : Exception(message)

// Exception thrown during Payment Creation processes
class PaymentCreationException (message: String) : Exception(message)

// Exception thrown during Task Creation processes
class PaymentDeletionException (message: String) : Exception(message)

// Exception thrown during Guest Creation processes
class GuestCreationException (message: String) : Exception(message)

// Exception thrown during Guest Creation processes
class GuestDeletionException (message: String) : Exception(message)

// Exception thrown during Vendor Creation processes
class VendorCreationException (message: String) : Exception(message)

// Exception thrown during Vendor Creation processes
class VendorDeletionException (message: String) : Exception(message)

class EventNotFoundException (message: String, val firebaseUser: FirebaseUser) : Exception(message)

class EventCreationException (message: String) : Exception(message)

class CalendarCreationException (message: String) : Exception(message)
class CalendarEditionException (message: String) : Exception(message)
class UserRetrievalException (message: String) : Exception(message)
