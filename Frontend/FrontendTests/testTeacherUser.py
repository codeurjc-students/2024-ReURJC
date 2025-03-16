from selenium import webdriver
from selenium.common import TimeoutException, NoSuchElementException
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import pytest
import datetime

# Driver setup as fixture to reuse across tests
@pytest.fixture
def driver():
    driver = webdriver.Chrome()
    yield driver
    driver.quit()

# Reusable function to perform login
def perform_login(driver, email, password):
    """Performs login with the provided credentials."""
    driver.get("http://localhost:8080")
    wait = WebDriverWait(driver, 10)
    email_field = wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, 'ion-input[name="email"] input')))
    email_field.send_keys(email)
    password_field = wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, 'ion-input[name="password"] input')))
    password_field.send_keys(password)
    login_button = wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, 'ion-button[type="submit"]')))
    login_button.click()

# Test 1: Verify professor login
def test_professor_login(driver):
    """Test to verify that professor login works correctly."""
    perform_login(driver, "yaovi123@icloud.com", "123")

    # Verify that login was successful by looking for a main page element
    wait = WebDriverWait(driver, 10)
    services_title = wait.until(EC.presence_of_element_located((By.XPATH, "//ion-title[contains(text(), 'Servicios')]")))
    assert services_title is not None, "The title 'Servicios' was not found after login"
    print("Test passed: Successful professor login")

# Test 2: Create attendance as professor
def test_create_attendance(driver):
    """Test to create attendance as a professor and verify its creation."""
    # Step 1: Login as professor
    perform_login(driver, "yaovi123@icloud.com", "123")

    # Step 2: Navigate to the attendance creation section
    wait = WebDriverWait(driver, 10)
    create_attendance_button = wait.until(
        EC.element_to_be_clickable((By.XPATH, "//ion-card-title[contains(text(), 'Crear asistencia')]")))
    create_attendance_button.click()

    # Wait for the URL to change to the attendance page
    wait.until(EC.url_contains("asistencia"))

    # Step 3: Select the subject "Historia"
    select_subject = wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, "ion-select")))
    select_subject.click()

    # Wait for the alert to appear and select "Historia"
    alert = wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, "ion-alert")))
    historia_option = wait.until(EC.element_to_be_clickable((By.XPATH, "//button[contains(., 'Historia')]")))
    historia_option.click()

    # Confirm the selection
    ok_button = wait.until(
        EC.element_to_be_clickable((By.CSS_SELECTOR, "ion-alert button.alert-button:not(.alert-button-role-cancel)")))
    ok_button.click()
    wait.until(EC.staleness_of(alert))

    # Step 4: Create the attendance
    create_attendance_submit = wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, 'ion-button[expand="block"]')))
    create_attendance_submit.click()

    # Step 5: Verify that the attendance has been created by looking for a "Historia" card
    historia_card = wait.until(
        EC.presence_of_element_located((By.XPATH, "//ion-card-title[contains(text(), 'Historia')]")))
    assert historia_card is not None, "The 'Historia' card was not found after creating attendance"
    print("Test passed: Attendance created and verified successfully")

def select_court_type(driver, court_type):
    """Selects the type of court (Tennis, Basketball, Soccer)."""
    wait = WebDriverWait(driver, 10)
    # Locate AND CLICK in a single line (or in very close steps).
    wait.until(EC.element_to_be_clickable(
      (By.XPATH, f"//ion-item[contains(., '{court_type}')]")
    )).click()

def month_string_to_number(month_str):
    """Converts a month name (in Spanish) to its number (1-12)."""
    months = {
      "enero": 1, "febrero": 2, "marzo": 3, "abril": 4, "mayo": 5, "junio": 6,
      "julio": 7, "agosto": 8, "septiembre": 9, "octubre": 10, "noviembre": 11, "diciembre": 12
    }
    return months[month_str.lower()]

def select_date(driver, year, month, day):
    """Selects a date in the calendar."""
    wait = WebDriverWait(driver, 10)

    # Debugging: Print current HTML to check state
    print("DEBUG: Current HTML before searching for the title:", driver.page_source[:1000])

    # Find the calendar title within the active ion-col
    try:
        title_element = wait.until(
          EC.visibility_of_element_located(
            (By.XPATH, "//ion-col[contains(@class, 'active')]//ion-card-title[contains(text(), '2025')]")
          )
        )
    except TimeoutException:
        print("DEBUG: Calendar title not found. Current HTML:", driver.page_source[:1000])
        raise TimeoutException(
          "Could not locate the calendar title 'March 2025'. Verify that the calendar is visible.")

    current_month_year_str = title_element.text.strip()
    print(f"DEBUG: current_month_year_str = '{current_month_year_str}'")

    # Extract current month and year
    current_month_str, current_year_str = current_month_year_str.split()
    current_year = int(current_year_str)
    current_month = month_string_to_number(current_month_str)

    # Navigate if necessary (adjust the XPaths of the buttons if they exist)
    while current_year != year or current_month != month:
        if current_year < year or (current_year == year and current_month < month):
            next_button = wait.until(EC.element_to_be_clickable((By.XPATH, "//ion-button[contains(., 'Siguiente')]")))
            next_button.click()
        elif current_year > year or (current_year == year and current_month > month):
            prev_button = wait.until(EC.element_to_be_clickable((By.XPATH, "//ion-button[contains(., 'Anterior')]")))
            prev_button.click()

        title_element = wait.until(
          EC.visibility_of_element_located(
            (By.XPATH, "//ion-col[contains(@class, 'active')]//ion-card-title[contains(text(), '2025')]")
          )
        )
        current_month_year_str = title_element.text.strip()
        current_month_str, current_year_str = current_month_year_str.split()
        current_year = int(current_year_str)
        current_month = month_string_to_number(current_month_str)

    # Select the day
    day_xpath = f"//ion-col[contains(@class, 'active')]//span[@tappable and contains(@class, 'week') and contains(text(), ' {day} ')]"
    try:
        day_element = wait.until(EC.element_to_be_clickable((By.XPATH, day_xpath)))
        day_element.click()
        print(f"DEBUG: Day {day} selected.")
    except TimeoutException:
        print(f"DEBUG: Could not select day {day}. Current HTML:", driver.page_source[:1000])
        raise TimeoutException(f"Could not find day {day} on the calendar.")

def select_time(driver, hour, minute):
    """Selects a time in the schedule list."""
    wait = WebDriverWait(driver, 10)
    # Format time string without leading zero for single-digit hours
    time_str = f"{hour}:{minute:02d}"  # e.g., "9:00" instead of "09:00"
    # XPath to match the time slot button, accounting for whitespace
    button_xpath = f"//ion-button[contains(., '{time_str} -')]"

    try:
        # Debugging: Print current HTML before searching for the time
        print(f"DEBUG: Searching for time {time_str}. Current HTML:", driver.page_source[:1000])

        time_button = wait.until(EC.element_to_be_clickable((By.XPATH, button_xpath)))
        time_button.click()
        print(f"DEBUG: Time {time_str} selected.")
    except TimeoutException:
        print(f"DEBUG: Could not find the button for time {time_str}. Current HTML:", driver.page_source[:1000])
        pytest.fail(
          f"Could not find the button for time {time_str}. Check the XPath or if the schedules are visible.")
    except NoSuchElementException:
        pytest.fail(f"The element for time {time_str} does not exist on the page.")

def test_court_reservation(driver):
    """Complete test of court reservation with automatic submission."""
    perform_login(driver, "yaovi123@icloud.com", "123")  # Login
    wait = WebDriverWait(driver, 10)

    # 1. Navigate to the services page
    services_tab = wait.until(EC.element_to_be_clickable((By.XPATH, "//ion-tab-button[@tab='services']")))
    services_tab.click()

    # 2. Navigate to the court reservation page
    button = wait.until(EC.element_to_be_clickable(
      (By.XPATH, "//ion-tab[@tab='services']//ion-card-title[contains(., 'Reserva de cancha')]")
    ))
    button.click()
    wait.until(EC.url_to_be("http://localhost:8080/reservations"))

    # 3. Select the court type
    select_court_type(driver, "Pista de fútbol (Móstoles)")

    # 4. Select the date
    value = datetime.datetime.now().day + 1
    select_date(driver, 2025, 3, value)  # Year, Month, Day

    # 5. Select the time (triggers the POST request automatically)
    select_time(driver, 9, 0)  # 9:00 - 11:00

    # 6. Verify the result in the UI
    try:
        # Wait for the "Active Reservation" card to appear
        reservation_card = wait.until(EC.visibility_of_element_located(
          (By.XPATH, "//ion-card-header/ion-card-title[contains(text(), 'Reserva Activa')]")
        ))
        assert "Reserva Activa" in reservation_card.text, "The title 'Reserva Activa' was not found."

        print("Test passed: Active reservation detected with correct details.")
    except TimeoutException:
        # Print HTML for debugging if it fails
        print("DEBUG: 'Active Reservation' not found. Current HTML:", driver.page_source[:2000])
        pytest.fail("The 'Active Reservation' card was not detected after the reservation.")

def test_navigate_to_calendar(driver):
    """Tests navigation to the Calendar page from the Times tab."""
    perform_login(driver, "yaovi123@icloud.com", "123")  # Login
    wait = WebDriverWait(driver, 10)
    # First, go to the schedules tab
    times_tab = wait.until(EC.element_to_be_clickable((By.XPATH, "//ion-tab-button[@tab='schedules']")))
    times_tab.click()
    # Now search for the calendar
    calendar_button = wait.until(EC.element_to_be_clickable(
        (By.XPATH, "//ion-tab[@tab='schedules']//ion-card-title[contains(text(), 'Calendario')]/ancestor::ion-card")
    ))
    calendar_button.click()
    wait.until(EC.url_to_be("http://localhost:8080/calendar"))
    assert driver.current_url == "http://localhost:8080/calendar"
    print("Test passed: Navigation to Calendar")

def test_navigate_to_schedule(driver):
    """Tests navigation to the Schedule page from the Times tab."""
    perform_login(driver, "yaovi123@icloud.com", "123")
    wait = WebDriverWait(driver, 10)
    # First, go to the schedules tab
    times_tab = wait.until(EC.element_to_be_clickable((By.XPATH, "//ion-tab-button[@tab='schedules']")))
    times_tab.click()

    schedule_button = wait.until(EC.element_to_be_clickable(
        (By.XPATH, "//ion-tab[@tab='schedules']//ion-card-title[contains(text(), 'Horario')]/ancestor::ion-card")
    ))
    schedule_button.click()
    wait.until(EC.url_to_be("http://localhost:8080/schedule"))
    assert driver.current_url == "http://localhost:8080/schedule"
    print("Test passed: Navigation to Schedule")
