import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { IonicModule } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginPage } from './login.page';
import { ApiAuthService } from '../services/AuthService/api-auth-service.service';

describe('LoginPage', () => {
  let component: LoginPage;
  let fixture: ComponentFixture<LoginPage>;
  let apiServiceSpy: jasmine.SpyObj<ApiAuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  // Configuración antes de cada prueba
  beforeEach(waitForAsync(() => {
    // Crear mocks para ApiAuthService y Router
    apiServiceSpy = jasmine.createSpyObj('ApiAuthService', ['login']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    // Configurar el módulo de pruebas
    TestBed.configureTestingModule({
      declarations: [LoginPage],
      imports: [
        IonicModule.forRoot(),
        FormsModule
      ],
      providers: [
        { provide: ApiAuthService, useValue: apiServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    // Crear el componente y detectar cambios
    fixture = TestBed.createComponent(LoginPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  // Prueba 1: Verificar que el componente se crea correctamente
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // Prueba 2: Verificar que se llama a login con las credenciales correctas
  it('should call apiService.login with correct credentials on submit', () => {
    // Simular entrada del usuario
    component.username = 'yaovi123@icloud.com';
    component.password = '1,2,3';

    // Simular el envío del formulario
    component.onSubmit();

    // Verificar que el método login fue llamado con los valores correctos
    expect(apiServiceSpy.login).toHaveBeenCalledWith('yaovi123@icloud.com', '1,2,3');
  });

  // Prueba 3: Verificar que no se llama a login si los campos están vacíos
  it('should not call apiService.login if fields are empty', () => {
    // Simular campos vacíos
    component.username = '';
    component.password = '';

    // Simular el envío del formulario
    component.onSubmit();

    // Verificar que el método login no fue llamado
    expect(apiServiceSpy.login).not.toHaveBeenCalled();
  });
});