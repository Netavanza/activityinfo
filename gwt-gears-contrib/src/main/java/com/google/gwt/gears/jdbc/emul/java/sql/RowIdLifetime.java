package java.sql;

import java.util.*;

public enum RowIdLifetime {

  /**
   * Indicates that this data source does not support the ROWID type.
   */
  ROWID_UNSUPPORTED,

  /**
   * Indicates that the lifetime of a RowId from this data source is indeterminate;
   * but not one of ROWID_VALID_TRANSACTION, ROWID_VALID_SESSION, or,
   * ROWID_VALID_FOREVER.
   */
  ROWID_VALID_OTHER,

  /**
   * Indicates that the lifetime of a RowId from this data source is at least the
   * containing session.
   */
  ROWID_VALID_SESSION,

  /**
   * Indicates that the lifetime of a RowId from this data source is at least the
   * containing transaction.
   */
  ROWID_VALID_TRANSACTION,

  /**
   * Indicates that the lifetime of a RowId from this data source is, effectively,
   * unlimited.
   */
  ROWID_VALID_FOREVER
}