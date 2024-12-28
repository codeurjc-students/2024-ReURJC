import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ApiAuthService } from 'src/app/services/AuthService/api-auth-service.service';

@Component({
  selector: 'app-auth-form',
  templateUrl: './auth-form.component.html',
  styleUrls: ['./auth-form.component.scss'],
})
export class AuthFormComponent {

  username: string = '';
    password: string = '';
  
    constructor(private apiService: ApiAuthService, private router: Router) { }

  
    onSubmit() {
      if (this.username !== '' && this.password !== '') {
        this.apiService.login(this.username, this.password);
      }
    }

}
