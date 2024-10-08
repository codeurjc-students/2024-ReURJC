import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { HomePage } from './home.page';
import { FinalsInfoComponent } from './finals-info/finals-info.component';
import { AuthGuard } from '../services/AuthService/auth-guard.component';

const routes: Routes = [
  {
    path: '',
    component: HomePage
  },
  {path: 'me/subjects',
    component: FinalsInfoComponent,
    canActivate: [AuthGuard]
    }, 
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class HomePageRoutingModule {}
