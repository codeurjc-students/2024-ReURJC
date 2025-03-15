import pytest
import random
import string
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.alert import Alert
from selenium.common.exceptions import TimeoutException, NoSuchElementException
import re

# Fixture to manage the WebDriver lifecycle
@pytest.fixture(scope="function")  # CRUCIAL CHANGE: scope="function"
def driver():
    """
    Initializes the Chrome WebDriver, maximizes the window,
    yields the driver for the test, and quits it afterward.
    """
    driver = webdriver.Chrome()
    driver.maximize_window()  # Maximize the window
    yield driver
    driver.quit()

# Helper function to generate random strings
def generate_random_string(length):
    """
    Generates a random string of specified length using lowercase letters.

    Args:
        length (int): The desired length of the random string.

    Returns:
        str: The generated random string.
    """
    letters = string.ascii_lowercase
    return ''.join(random.choice(letters) for _ in range(length))

# Helper function to perform login steps
def perform_login(driver, email, password):
    """
    Performs the login steps, with redirection verification.

    Args:
        driver: The Selenium WebDriver instance.
        email (str): The user's email address.
        password (str): The user's password.

    Raises:
        pytest.fail: If login or redirection fails.
    """
    driver.get("http://localhost:8080")
    wait = WebDriverWait(driver, 10)
    try:
        email_input = wait.until(EC.visibility_of_element_located((By.CSS_SELECTOR, 'ion-input[name="email"] input')))
        email_input.clear()
        email_input.send_keys(email)

        password_input = wait.until(EC.visibility_of_element_located((By.CSS_SELECTOR, 'ion-input[name="password"] input')))
        password_input.clear()
        password_input.send_keys(password)

        login_button = wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, 'ion-button[type="submit"]')))
        login_button.click()

        # Redirection verification *inside* perform_login
        wait.until(EC.url_to_be("http://localhost:8080/home"), "Login failed or did not redirect to /home")

    except (TimeoutException, NoSuchElementException) as e:
        print(f"Error in perform_login: {str(e)}")
        pytest.fail(f"Error in perform_login: {str(e)}")  # Explicitly fail the test


# We NO LONGER need reset_to_login, because each test has its own driver.

# Test for the Selenium main page (Maintained)
@pytest.mark.selenium
def test_selenium(driver):
    """Tests navigation to the Selenium main page and verifies the title."""
    driver.get("https://www.selenium.dev/")
    assert driver.title == "Selenium"
    print("Test passed: Navigation to Selenium")

# Test for successful login
def test_login_correct_data(driver):
    """Tests login with correct credentials."""
    perform_login(driver, "yaovi@icloud.com", "123")  # Already verifies redirection
    print("Test passed: Successful login")

# Test for login with incorrect email
def test_incorrect_email(driver):
    """Tests login with an incorrect email, expecting an alert."""
    driver.get("http://localhost:8080")  # Navigate to the page in each test
    wait = WebDriverWait(driver, 10)

    random_email = generate_random_string(10) + "@example.com"
    email_input = wait.until(EC.visibility_of_element_located((By.CSS_SELECTOR, 'ion-input[name="email"] input')))
    email_input.clear()
    email_input.send_keys(random_email)

    password_input = wait.until(EC.visibility_of_element_located((By.CSS_SELECTOR, 'ion-input[name="password"] input')))
    password_input.clear()
    password_input.send_keys("123")

    login_button = wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, 'ion-button[type="submit"]')))
    login_button.click()

    alert = wait.until(EC.alert_is_present())
    assert "Wrong credentials" in alert.text
    alert.accept()
    print("Test passed: Alert with incorrect email")

# Test for login with incorrect password (similar structure)
def test_incorrect_password(driver):
    """Tests login with an incorrect password, expecting an alert."""
    driver.get("http://localhost:8080")  # Navigate to the page in each test
    wait = WebDriverWait(driver, 10)
    correct_email = "yaovi@icloud.com"
    random_password = generate_random_string(8)

    email_input = wait.until(EC.visibility_of_element_located((By.CSS_SELECTOR, 'ion-input[name="email"] input')))
    email_input.clear()
    email_input.send_keys(correct_email)

    password_input = wait.until(EC.visibility_of_element_located((By.CSS_SELECTOR, 'ion-input[name="password"] input')))
    password_input.clear()
    password_input.send_keys(random_password)

    login_button = wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, 'ion-button[type="submit"]')))
    login_button.click()

    alert = wait.until(EC.alert_is_present())
    assert "Wrong credentials" in alert.text
    alert.accept()
    print("Test passed: Alert with incorrect password")

# Navigation tests (with improved selectors and without reset_to_login)
def test_bluetooth_attendance(driver):
    """Tests navigation to the Bluetooth Attendance page."""
    perform_login(driver, "yaovi@icloud.com", "123")  # Isolated login
    wait = WebDriverWait(driver, 10)
    button = wait.until(EC.element_to_be_clickable(
        (By.XPATH, "//ion-tab[@tab='services']//ion-card-title[contains(., 'Asistencia Bluetooth')]")
    ))
    button.click()
    wait.until(EC.url_to_be("http://localhost:8080/asistencia"))
    assert driver.current_url == "http://localhost:8080/asistencia"
    print("Test passed: Navigation to Bluetooth Attendance")

def test_court_reservation(driver):
    """Tests navigation to the Court Reservation page."""
    perform_login(driver, "yaovi@icloud.com", "123")
    wait = WebDriverWait(driver, 10)
    button = wait.until(EC.element_to_be_clickable(
        (By.XPATH, "//ion-tab[@tab='services']//ion-card-title[contains(., 'Reserva de cancha')]")
    ))
    button.click()
    wait.until(EC.url_to_be("http://localhost:8080/reservations"))
    assert driver.current_url == "http://localhost:8080/reservations"
    print("Test passed: Navigation to Court Reservation")

def test_become_candidate(driver):
    """Tests navigation to the Become a Candidate page."""
    perform_login(driver, "yaovi@icloud.com", "123")
    wait = WebDriverWait(driver, 10)
    button = wait.until(EC.element_to_be_clickable(
        (By.XPATH, "//ion-tab[@tab='services']//ion-card-title[contains(., 'Postularse como candidato')]")
    ))
    button.click()
    wait.until(EC.url_to_be("http://localhost:8080/becomeDelegate"))
    assert driver.current_url == "http://localhost:8080/becomeDelegate"
    print("Test passed: Navigation to Become a Candidate")

def test_vote_delegates(driver):
    """Tests navigation to the Vote for Delegates page."""
    perform_login(driver, "yaovi@icloud.com", "123")
    wait = WebDriverWait(driver, 10)
    button = wait.until(EC.element_to_be_clickable(
        (By.XPATH, "//ion-tab[@tab='services']//ion-card-title[contains(., 'Voto a delegados')]")
    ))
    button.click()
    wait.until(EC.url_to_be("http://localhost:8080/voteDelegate"))
    assert driver.current_url == "http://localhost:8080/voteDelegate"
    print("Test passed: Navigation to Vote for Delegates")


def test_navigate_to_calendar(driver):
    """Tests navigation to the Calendar page from the Times tab."""
    perform_login(driver, "yaovi@icloud.com", "123")  # Login
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
    perform_login(driver, "yaovi@icloud.com", "123")
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


def test_navigate_to_final_exams(driver):
    """Tests navigation to the Final Exams page from the Times tab."""
    perform_login(driver, "yaovi@icloud.com", "123")
    wait = WebDriverWait(driver, 10)
    # First, go to the schedules tab
    times_tab = wait.until(EC.element_to_be_clickable((By.XPATH, "//ion-tab-button[@tab='schedules']")))
    times_tab.click()

    exams_button = wait.until(EC.element_to_be_clickable(
      (By.XPATH, "//ion-tab[@tab='schedules']//ion-card-title[contains(text(), 'Exámenes finales')]/ancestor::ion-card")
    ))
    exams_button.click()
    wait.until(EC.url_to_be("http://localhost:8080/me/subjects"))
    assert driver.current_url == "http://localhost:8080/me/subjects"
    print("Test passed: Navigation to Final Exams")


def test_navigate_to_calificaciones_finales(driver):
    """Tests the navigation to the Final Grades page from the Profile tab."""
    perform_login(driver, "yaovi@icloud.com", "123")
    wait = WebDriverWait(driver, 10)
    profile_tab = wait.until(EC.element_to_be_clickable((By.XPATH, "//ion-tab-button[@tab='profile']")))
    profile_tab.click()


    exams_button = wait.until(EC.element_to_be_clickable((
      By.XPATH,
      "//ion-tab[@tab='profile']"  # Search within the profile tab
      "//ion-card-title[contains(text(),'Calificaciones finales')]"
      "/ancestor::ion-card"
    )))
    exams_button.click()
    wait.until(EC.url_to_be("http://localhost:8080/me/finalMarks"))
    assert driver.current_url == "http://localhost:8080/me/finalMarks"
    print("Test passed: Navigation to Final Grades")


def month_string_to_number(month_str):
    """Converts a month name (in Spanish) to its number (1-12)."""
    months = {
      "enero": 1, "febrero": 2, "marzo": 3, "abril": 4, "mayo": 5, "junio": 6,
      "julio": 7, "agosto": 8, "septiembre": 9, "octubre": 10, "noviembre": 11, "diciembre": 12
    }
    return months[month_str.lower()]


def select_court_type(driver, court_type):
    """Selects the type of court (Tennis, Basketball, Soccer)."""
    wait = WebDriverWait(driver, 10)
    # Locate AND CLICK in a single line (or in very close steps).
    wait.until(EC.element_to_be_clickable(
      (By.XPATH, f"//ion-item[contains(., '{court_type}')]")
    )).click()

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
    perform_login(driver, "yaovi@icloud.com", "123")  # Login
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
    select_date(driver, 2025, 3, 16)  # Year, Month, Day

    # 5. Select the time (triggers the POST request automatically)
    select_time(driver, 9, 0)  # 9:00 - 11:00

    # 6. Verify the result in the UI
    try:
      # Wait for the "Reserva Activa" card to appear
      reservation_card = wait.until(EC.visibility_of_element_located(
        (By.XPATH, "//ion-card-header/ion-card-title[contains(text(), 'Reserva Activa')]")
      ))
      assert "Reserva Activa" in reservation_card.text, "The title 'Reserva Activa' was not found."


      print("Test passed: Active reservation detected with correct details.")
    except TimeoutException:
      # Print HTML for debugging if it fails
      print("DEBUG: 'Reserva Activa' not found. Current HTML:", driver.page_source[:2000])
      pytest.fail("The 'Reserva Activa' card was not detected after the reservation.")


def test_become_candidate(driver):
    """Tests the registration and cancellation of the candidacy."""
    perform_login(driver, "yaovi@icloud.com", "123")  # Login.
    wait = WebDriverWait(driver, 10)

    # 1. Navigate to the services page
    services_tab = wait.until(EC.element_to_be_clickable((By.XPATH, "//ion-tab-button[@tab='services']")))
    services_tab.click()

    # 2. Navigate to "Become a candidate"
    become_candidate_button = wait.until(EC.element_to_be_clickable(
      (By.XPATH, "//ion-tab[@tab='services']//ion-card-title[contains(text(), 'Postularse como candidato')]")
    ))
    become_candidate_button.click()
    wait.until(EC.url_to_be("http://localhost:8080/becomeDelegate"))

    # 3. Click on the "Inscribirse" button
    inscribirse_button = wait.until(EC.element_to_be_clickable((By.XPATH, "//ion-button[contains(., 'Inscribirse')]")))
    inscribirse_button.click()


    print("Test passed: Become a candidate (registration)")


def test_create_attendance_and_extract_code():
    """
    Test to create an attendance, extract the code, log in as another user,
    and register attendance with the code.
    """
    driver = webdriver.Chrome()
    wait = WebDriverWait(driver, 10)

    try:
      # --- Part 1: Create the attendance (as yaovi123) ---
      perform_login(driver, "yaovi123@icloud.com", "123")

      crear_asistencia_button = wait.until(
        EC.element_to_be_clickable((By.XPATH, "//ion-card-title[contains(text(), 'Crear asistencia')]")))
      crear_asistencia_button.click()
      wait.until(EC.url_contains("asistencia"))

      select_materia = wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, "ion-select")))
      select_materia.click()

      alert = wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, "ion-alert")))
      historia_option = wait.until(EC.element_to_be_clickable((By.XPATH, "//button[contains(., 'Historia')]")))
      historia_option.click()

      ok_button = wait.until(
        EC.element_to_be_clickable((By.CSS_SELECTOR, "ion-alert button.alert-button:not(.alert-button-role-cancel)")))
      ok_button.click()
      wait.until(EC.staleness_of(alert))

      crear_asistencia_submit = wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, 'ion-button[expand="block"]')))
      crear_asistencia_submit.click()

      codigo_elemento = wait.until(EC.presence_of_element_located((By.XPATH, "//p[strong[contains(text(), 'Código:')]]")))

      codigo_texto = codigo_elemento.text
      match = re.search(r"Código:\s*(\w+)", codigo_texto)
      if match:
        codigo_asistencia = match.group(1)
        print(f"Attendance code: {codigo_asistencia}")
      else:
        codigo_asistencia = None
        pytest.fail("Could not find the attendance code on the page.")

      assert codigo_asistencia is not None, "The attendance code should not be None"
      assert len(codigo_asistencia) > 0, "The attendance code should not be empty"
      assert codigo_asistencia.isalnum(), "The attendance code should be alphanumeric"

      # --- Part 2:  Log out and log in again (as yaovi) ---

      driver.quit()  # Close the browser *completely*.
      driver = webdriver.Chrome()  # Create a *new* driver instance.
      wait = WebDriverWait(driver, 10)  # and a new wait instance.

      perform_login(driver, "yaovi@icloud.com", "123")  # New login.

      # --- Part 3: Navigate to "Bluetooth Attendance" ---
      asistencia_bluetooth_button = wait.until(
        EC.element_to_be_clickable((By.XPATH, "//ion-card-title[contains(text(), 'Asistencia Bluetooth')]")))
      asistencia_bluetooth_button.click()
      wait.until(EC.url_contains("asistencia"))

      # --- Part 4: Enter the code and register ---
      codigo_input = wait.until(
        EC.presence_of_element_located((By.CSS_SELECTOR, "ion-input[placeholder='Introduce el código'] input")))
      codigo_input.send_keys(codigo_asistencia)

      # Use JavaScript to click the "Registrar" button
      registrar_button = wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, "ion-button[expand='block']")))
      driver.execute_script("arguments[0].click();", registrar_button)

      # --- Part 5: Verification of successful registration ---
      # Wait for the success toast to appear.  This is the *definitive* check.
      wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, "ion-toast.ion-color-success")))


    except (TimeoutException, NoSuchElementException) as e:
      pytest.fail(f"Test failed: {str(e)}")
    finally:
      driver.quit()
