package Application

// Exceptions related to User Login and Authentication
class EmailVerificationException (message: String) : Exception(message)
class UserAuthenticationException (message: String) : Exception(message)
class SessionAccessException (message: String) : Exception(message)
class ExistingSessionException (message: String) : Exception(message)

// Exception related to Database updates
class FirebaseDataImportException (message: String) : Exception(message)