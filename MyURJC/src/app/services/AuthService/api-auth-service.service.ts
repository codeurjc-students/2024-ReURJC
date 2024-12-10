import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
<<<<<<< HEAD
import { Observable, tap } from 'rxjs';
=======
import { BehaviorSubject } from 'rxjs';
>>>>>>> origin/feature/reservations
import { Router } from '@angular/router';
import { User } from '../UserService/user.model';

@Injectable({
  providedIn: 'root'
})
export class ApiAuthService {
  apiUrl: string = "/api/login";
<<<<<<< HEAD
  
  private username: string = '';
  private user: User | undefined;
  private isLogged: boolean;
=======

  private username: string = '';
  private user: User | undefined;
  private loggedIn = new BehaviorSubject<boolean>(false);
  public loggedIn$ = this.loggedIn.asObservable();
  public fcmToken: string = "";
>>>>>>> origin/feature/reservations

  constructor(private http: HttpClient, private router: Router) {
    const storedUser = sessionStorage.getItem('user');
    if (storedUser) {
      this.user = JSON.parse(storedUser);
      this.username = this.user?.email as string;
<<<<<<< HEAD
      this.isLogged = true;
    } else {
      this.isLogged = false;
=======
      this.loggedIn.next(true)
    } else {
      this.loggedIn.next(false)
>>>>>>> origin/feature/reservations
    }
  }


  login(username: string, password: string) {
    this.username = username;
    const formData = { username: this.username, password: password };
<<<<<<< HEAD
    this.http.post("/api/users/login", formData, { withCredentials: true }).subscribe({
      next: (response: any) => { this.reqIsLogged() },
=======
    this.http.post("/api/users/login", formData, {
      withCredentials: true,

    }).subscribe({
      next: (response: any) => { this.fcmToken == "" ? this.reqIsLogged() : this.sendToken(this.fcmToken) },
>>>>>>> origin/feature/reservations
      error: (error: any) => { alert("Wrong credentials") }
    });
  }

<<<<<<< HEAD
=======
  sendToken(token: string) {
    const url = `/api/users/setDeviceToken?fcmToken=${token}`; // Construye la URL con el parámetro

    this.http.post<any>(url, { withCredentials: true }) // Envía la solicitud POST con la URL modificada
      .subscribe({
        next: (response: any) => { this.reqIsLogged() },
        error: (error: any) => { alert("Wrong credentials") }
      });
  }

>>>>>>> origin/feature/reservations
  reqIsLogged() {
    this.http.get('/api/users/me', { withCredentials: true }).subscribe({
      next: (response: any) => {
        this.user = response as User;
<<<<<<< HEAD
        this.isLogged = true;
=======
        this.loggedIn.next(true)
>>>>>>> origin/feature/reservations
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
<<<<<<< HEAD
    return this.isLogged;
=======
    return this.loggedIn.getValue()
>>>>>>> origin/feature/reservations
  }

  isAdmin() {
    return this.user && this.user.roles.indexOf('ADMIN') !== -1;
  }
<<<<<<< HEAD

=======
  isTeacher() {
    return this.user && this.user.roles.indexOf('TEACHER') !== -1;
  }

  getUser():User | undefined {
    return this.user
  }
>>>>>>> origin/feature/reservations
}
