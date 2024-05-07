import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../components/navbar/navbar.component';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [NavbarComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  constructor(private router: Router, private cookieService: CookieService) { }
  user:UserDTO = JSON.parse(this.cookieService.get('token'));
  ngOnInit() {
  }
}
interface UserDTO{
  username:string;
  email:string;
  name:string;
  role:string;
  stats:StatsDTO;

}
interface StatsDTO{
  wins:number;
  losses:number;
  kills:number;
  deaths:number;
  hits:number;
  headshots:number;
  WR:number;
  KDR:number;
  HSp:number;
}
