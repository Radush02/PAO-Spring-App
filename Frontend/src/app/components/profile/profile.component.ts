import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import {saveAs} from 'file-saver';
@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [NavbarComponent],
  providers: [UserService],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  constructor(private router: Router, private cookieService: CookieService,private userService:UserService) { }
  user!:UserDTO;
  errorMessage="";
  async getUserStats(){
    this.userService.displayUser(JSON.parse(this.cookieService.get('token')).username).subscribe((data:UserDTO)=>
      {this.user=data;
        console.log(data);
      }
    );
  }
  uploadUser(){
    const fileInput = document.getElementById('file') as HTMLInputElement;
    const button = document.getElementById('statistici') as HTMLButtonElement;
    button.disabled = true;
    if (fileInput.files && fileInput.files.length > 0) {
      const file = fileInput.files[0];
      this.userService.uploadFile(file,this.user.username).subscribe(response => {
        console.log(response);
        button.disabled=false;
        this.errorMessage="Back-up incarcat cu succes";
      },error=>
        {
        this.errorMessage=error.error.split(': ')[1];
        button.disabled=false;
    });
    }
    else{
      button.disabled=false;
      alert('Incarca un fisier');
    }
  }
  downloadUser(){
    this.userService.downloadFile(this.user.username).subscribe(response => {
      //console.log(response.headers);
      const contentDispositionHeader = response.headers.get('Content-Disposition');
      //console.log(contentDispositionHeader);
      const filename = contentDispositionHeader
  ? contentDispositionHeader.split(';')[1].trim().split('=')[1].replace(/"/g, '')
  : 'contact_admin_issue.json';
      const blob = new Blob([response.body], { type: 'application/json' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
    });
  }
  ngOnInit() {
    this.getUserStats();
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
  wr:number;
  KDR:number;
  HSp:number;
}
