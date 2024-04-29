import { Component,inject,OnInit } from '@angular/core';
import { RouterOutlet,Router} from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClient,HttpClientModule} from '@angular/common/http';
import { FormBuilder,FormGroup,Validators,ReactiveFormsModule} from '@angular/forms';
import { UserService } from '../../services/user.service';
import { CookieService } from 'ngx-cookie-service';
//import { authGuard } from '../../guards/auth.guard';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  imports: [CommonModule,RouterOutlet,HttpClientModule,ReactiveFormsModule],
  providers:[UserService],
  styleUrls: ['./login.component.css'], 
  standalone: true
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  errorMessage="";
  constructor(private fb: FormBuilder, private httpClient: HttpClient,private router:Router,private loginService:UserService,private cookieService:CookieService) {
    this.loginForm=this.fb.group({
      username: ['',Validators.required],
      password: ['',[Validators.required,Validators.minLength(8)]],
      remember: [false,Validators.required]
    });
  }

  login() {
    this.loginService.login(this.loginForm.value).subscribe(
      (response: userDTO) => {
        this.cookieService.set
        ('token', JSON.stringify(response), undefined, '/', undefined, false, "Strict");
        this.errorMessage = 'Conectat';
        this.router.navigate(['/dashboard']);
      },
      (error: any) => {
        console.log(this.loginForm.value);
        console.error(error);
        this.errorMessage = error.error.split(': ')[1];
      }
    );
  }
  register(){
    this.router.navigate(['/register']);
  }
  ngOnInit(){
    this.loginForm=this.fb.group({
      username: ['',Validators.required],
      password: ['',[Validators.required,Validators.minLength(8)]],
      remember: [false,Validators.required]
    });
    setTimeout(() => {
      if (this.loginService.isLoggedIn()!="") {
        this.router.navigate(['/dashboard']);
      }
    });
  }
}  
interface userDTO{
    userId:string;
    username:string;
    email:string;
    name:string;
    role:string;
    stats:statsDTO;
}
interface statsDTO{
  wins: number;
  losses: number;
  kills: number;
  deaths: number;
  hits: number;
  headshots: number;
  WR: number;
  KDR: number;
  HSp: number;
}