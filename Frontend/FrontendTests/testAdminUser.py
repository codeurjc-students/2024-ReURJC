import pytest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Fixture to initialize and close the browser
@pytest.fixture
def driver():
    driver = webdriver.Chrome()
    yield driver
    driver.quit()

# Function to perform the admin login
def perform_login(driver, email, password):
    driver.get("http://localhost:8080")
    wait = WebDriverWait(driver, 10)
    # Locate and fill the email field
    email_field = wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, 'ion-input[name="email"] input')))
    email_field.send_keys(email)
    # Locate and fill the password field
    password_field = wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, 'ion-input[name="password"] input')))
    password_field.send_keys(password)
    # Click the login button
    login_button = wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, 'ion-button[type="submit"]')))
    login_button.click()

# Test for the "Court Reservations" button
def test_court_reservations(driver):
    # Perform login with admin credentials
    perform_login(driver, "yaovi1234@icloud.com", "123")
    wait = WebDriverWait(driver, 10)

    # Wait for the services page to load (verify "Services" title)
    services_title = wait.until(EC.presence_of_element_located((By.XPATH, "//ion-title[contains(text(), 'Servicios')]")))

    # Locate and click the "Court Reservations" button
    court_reservations_button = wait.until(
        EC.element_to_be_clickable((By.XPATH, "//ion-card-title[contains(text(), 'Reservas de cancha')]")))
    court_reservations_button.click()

    # Verify that the URL changes to the expected one
    wait.until(EC.url_to_be("http://localhost:8080/admin/reservationEvents"))
    assert driver.current_url == "http://localhost:8080/admin/reservationEvents", "Failed to redirect to the court reservations page"

# Test for the "Voting Events" button
def test_voting_events(driver):
    # Perform login with admin credentials
    perform_login(driver, "yaovi1234@icloud.com", "123")
    wait = WebDriverWait(driver, 10)

    # Wait for the services page to load (verify "Services" title)
    services_title = wait.until(EC.presence_of_element_located((By.XPATH, "//ion-title[contains(text(), 'Servicios')]")))

    # Locate and click the "Voting Events" button
    voting_events_button = wait.until(
        EC.element_to_be_clickable((By.XPATH, "//ion-card-title[contains(text(), 'Eventos de votos')]")))
    voting_events_button.click()

    # Verify that the URL changes to the expected one
    wait.until(EC.url_to_be("http://localhost:8080/admin/adminEvents"))
    assert driver.current_url == "http://localhost:8080/admin/adminEvents", "Failed to redirect to the voting events page"

# Test for navigating to the Calendar page
def test_navigate_to_calendar(driver):
    """Tests navigation to the Calendar page from the Times tab."""
    perform_login(driver, "yaovi1234@icloud.com", "123")  # Login
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
