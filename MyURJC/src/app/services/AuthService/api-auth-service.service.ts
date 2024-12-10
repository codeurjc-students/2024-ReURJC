import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import { User } from '../UserService/user.model';

@Injectable({
  providedIn: 'root'
})
export class ApiAuthService {
  apiUrl: string = "/api/login";

  private username: string = '';
  private user: User | undefined;
  private loggedIn = new BehaviorSubject<boolean>(false);
  public loggedIn$ = this.loggedIn.asObservable();
  public fcmToken: string = "";

  constructor(private http: HttpClient, private router: Router) {
    const storedUser = sessionStorage.getItem('user');
    if (storedUser) {
      this.user = JSON.parse(storedUser);
      this.username = this.user?.email as string;
      this.loggedIn.next(true)
    } else {
      this.loggedIn.next(false)
    }
  }


  login(username: string, password: string) {
    this.username = username;
    const formData = { username: this.username, password: password };
    this.http.post("/api/users/login", formData, {
      withCredentials: true,

    }).subscribe({
      next: (response: any) => { this.fcmToken == "" ? this.reqIsLogged() : this.sendToken(this.fcmToken) },
      error: (error: any) => { alert("Wrong credentials") }
    });
  }

  sendToken(token: string) {
    const url = `/api/users/setDeviceToken?fcmToken=${token}`; // Construye la URL con el parámetro

    this.http.post<any>(url, { withCredentials: true }) // Envía la solicitud POST con la URL modificada
      .subscribe({
        next: (response: any) => { this.reqIsLogged() },
        error: (error: any) => { alert("Wrong credentials") }
      });
  }

  reqIsLogged() {
    this.http.get('/api/users/me', { withCredentials: true }).subscribe({
      next: (response: any) => {
        this.user = response as User;
        this.loggedIn.next(true)
        sessionStorage.setItem('user', JSON.stringify(this.user));
        this.router.navigate(['/']);
      },
      error: (error: any) => {
        if (error.status != 404) {
          console.error('Error when asking if logged: ' + JSON.stringify(error));
        }
      }
    });
  }

  isLoggedIn(): boolean {
    return this.loggedIn.getValue()
  }

  isAdmin() {
    return this.user && this.user.roles.indexOf('ADMIN') !== -1;
  }
  isTeacher() {
    return this.user && this.user.roles.indexOf('TEACHER') !== -1;
  }

  getUser():User | undefined {
    return this.user
  }
}
