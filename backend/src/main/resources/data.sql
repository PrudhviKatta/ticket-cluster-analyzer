INSERT INTO tickets (id, title, description, submitted_by, created_at, cluster_label) VALUES

-- Authentication Failures
(1,  'Cannot log in after password reset',
     'Password reset link was clicked but login still fails. Browser shows invalid credentials even with the new password.',
     'alice@corp.com', '2024-03-01 09:12:00', NULL),

(2,  'Two-factor authentication code not received',
     'SMS code for 2FA never arrives. Waited 10 minutes, tried resend 3 times. Cannot access account.',
     'bob@startup.io', '2024-03-02 11:34:00', NULL),

(3,  'Account locked after failed login attempts',
     'After 3 wrong password tries, account got locked. No unlock email was ever sent to the registered address.',
     'carol@enterprise.net', '2024-03-03 14:05:00', NULL),

(4,  'SSO with Okta failing for all enterprise users',
     'Since yesterday morning, our entire team cannot log in via Okta SSO. Error: SAML assertion invalid.',
     'dave@bigcorp.com', '2024-03-04 08:45:00', NULL),

(5,  'Session expires after 5 minutes of activity',
     'Users are being logged out after only 5 minutes even while actively using the app. Very disruptive.',
     'eve@agency.co', '2024-03-05 16:20:00', NULL),

(6,  'Login page redirects to 404 after authentication',
     'Entering correct credentials shows success briefly, then redirects to a 404 page. Happens consistently on Chrome.',
     'frank@retailco.com', '2024-03-06 10:33:00', NULL),

(7,  'OAuth token refresh failing silently',
     'Users get logged out unexpectedly mid-session. No error shown. Appears to be silent token refresh failure.',
     'grace@techfirm.dev', '2024-03-07 13:50:00', NULL),

-- Performance Issues
(8,  'Dashboard takes 45 seconds to load',
     'Main dashboard with large datasets is extremely slow. Network tab shows a single API call taking 43s.',
     'henry@logistics.com', '2024-03-01 10:00:00', NULL),

(9,  'Search results page times out for large queries',
     'Any search returning more than 1000 results throws a 504 Gateway Timeout. Worked fine last month.',
     'iris@healthtech.io', '2024-03-02 12:15:00', NULL),

(10, 'API response times degraded after last deployment',
     'Since the v2.4 deploy on Feb 28, average API latency jumped from 120ms to 1800ms. All endpoints affected.',
     'jack@fintech.co', '2024-03-03 09:30:00', NULL),

(11, 'Report generation freezes browser for large date ranges',
     'Generating reports for date ranges over 30 days freezes the browser tab. Users have to force-refresh.',
     'kate@mediagroup.com', '2024-03-04 15:45:00', NULL),

(12, 'Bulk CSV import takes 20+ minutes for 500 rows',
     'Importing a 500-row CSV file should take seconds but hangs at 0% for 20 minutes then times out.',
     'liam@ecommerce.net', '2024-03-05 11:20:00', NULL),

(13, 'Product page images load one by one very slowly',
     'Image thumbnails on the product listing page load sequentially instead of in parallel. Page feels broken.',
     'mia@retailapp.com', '2024-03-06 14:10:00', NULL),

(14, 'Database queries timing out during peak hours',
     'Every weekday between 9-11am EST, queries start timing out. DB CPU spikes to 100%. Normal off-peak.',
     'noah@saasplatform.io', '2024-03-07 09:05:00', NULL),

-- Billing Problems
(15, 'Charged twice for March subscription',
     'Credit card statement shows two charges of $99 on March 1st. Only one subscription is active on account.',
     'olivia@consulting.com', '2024-03-01 14:30:00', NULL),

(16, 'Invoice shows incorrect tax rate for EU customers',
     'All invoices for EU accounts show 0% VAT. We are required to charge 20% VAT. This is a compliance issue.',
     'peter@eupartner.de', '2024-03-02 10:45:00', NULL),

(17, 'Refund not processed after 2 weeks',
     'Refund was approved by support on Feb 20th. Confirmation email received but no credit has appeared.',
     'quinn@freelance.me', '2024-03-03 16:00:00', NULL),

(18, 'Promo code applied but full price charged',
     'Used promo code SAVE30 at checkout. Page confirmed 30% discount, but credit card charged full amount.',
     'rachel@smb.co', '2024-03-04 11:55:00', NULL),

(19, 'Cannot update credit card details',
     'Clicking "Update Payment Method" shows the form but submitting it throws a 422 validation error every time.',
     'sam@nonprofit.org', '2024-03-05 13:40:00', NULL),

(20, 'Downgraded plan but still charged premium rate',
     'Downgraded from Business to Starter on Feb 15th. Confirmation received but March invoice is still at $299.',
     'tina@growthco.com', '2024-03-06 09:20:00', NULL),

(21, 'Invoice PDF downloads as blank page',
     'Clicking download on any invoice downloads a PDF that opens as a completely blank white page.',
     'uma@designstudio.io', '2024-03-07 15:15:00', NULL),

-- Mobile Crashes
(22, 'App crashes on launch on iOS 17.4',
     'After updating iPhone to iOS 17.4, app crashes immediately on the splash screen. Clean reinstall did not help.',
     'victor@mobileuser.com', '2024-03-01 08:30:00', NULL),

(23, 'Push notifications freeze the app on Android 14',
     'Receiving a push notification while app is open causes it to freeze completely. Must force-quit and reopen.',
     'wendy@androiddev.net', '2024-03-02 09:50:00', NULL),

(24, 'Camera permission request crashes iPhone 15',
     'When the app requests camera access for profile photo upload, it crashes instantly on iPhone 15 Pro.',
     'xavier@photoshare.co', '2024-03-03 12:00:00', NULL),

(25, 'App crashes when switching tabs rapidly',
     'Tapping between the Home, Search, and Profile tabs quickly causes the app to crash after 3-4 switches.',
     'yara@appuser.io', '2024-03-04 14:25:00', NULL),

(26, 'Offline mode causes data loss on reconnect',
     'When working offline and reconnecting, all changes made offline disappear. No sync conflict resolution shown.',
     'zach@fieldworker.com', '2024-03-05 10:15:00', NULL),

(27, 'Dark mode toggle crashes Android app',
     'Switching between light and dark mode in settings crashes the app on Android 11 and below.',
     'amy@uxresearch.co', '2024-03-06 13:30:00', NULL),

(28, 'App crashes on receiving large file attachment',
     'In the chat feature, receiving any attachment over 5MB crashes the app. Sender gets no error either.',
     'ben@teamcomms.net', '2024-03-07 11:45:00', NULL),

-- Data Export Issues
(29, 'CSV export times out for date ranges over 90 days',
     'Exporting data for any period longer than 90 days results in a timeout error. 89 days works fine.',
     'chloe@analytics.io', '2024-03-01 11:00:00', NULL),

(30, 'Exported Excel file has corrupted last column',
     'Downloaded XLSX report has garbled characters in the final column. All other columns look correct.',
     'dan@dataops.com', '2024-03-02 14:20:00', NULL),

(31, 'PDF export missing all charts',
     'PDF reports only contain text and tables. All charts and visualizations are missing from the exported file.',
     'ella@marketingteam.co', '2024-03-03 10:40:00', NULL),

(32, 'Export queue stuck at processing for 24 hours',
     'Submitted a large export job yesterday morning. Status shows "Processing" but nothing has arrived by email.',
     'finn@dataplatform.io', '2024-03-04 16:50:00', NULL),

(33, 'Custom field column headers missing from CSV',
     'CSV exports of custom field data have correct values but no column headers in the first row.',
     'gina@crm.co', '2024-03-05 12:35:00', NULL),

(34, 'Export size limit too restrictive for enterprise',
     '10MB export limit is completely unworkable for our volume. We need at least 500MB. No enterprise option shown.',
     'hugo@enterprise.com', '2024-03-06 15:00:00', NULL),

(35, 'Scheduled export emails stopped arriving',
     'Weekly scheduled exports worked fine for months. Since March 1st no emails received. Settings unchanged.',
     'iris@reportingteam.net', '2024-03-07 10:25:00', NULL);
