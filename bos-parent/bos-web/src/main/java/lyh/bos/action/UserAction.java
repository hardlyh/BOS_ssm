package lyh.bos.action;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.ServletActionContext;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import lyh.bos.action.base.BaseAction;
import lyh.bos.domain.User;
import lyh.bos.service.IUserService;
import lyh.bos.utils.BOSUtils;
import lyh.bos.utils.MD5Utils;
import ooo.SayHelloImpl;

@Controller
@Scope("prototype")
public class UserAction extends BaseAction<User> {
    // 属性驱动，接收页面输入的验证码
    private String checkcode;

    @Autowired
    private SayHelloImpl helloImpl;

    public void setCheckcode(String checkcode) {
        this.checkcode = checkcode;
    }

    @Autowired
    private IUserService userService;

    public IUserService getUserService() {
        return userService;
    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    /*
     * 用户登录
     */

    public String login() {
        String validateCode = (String) ServletActionContext.getRequest().getSession().getAttribute("key");
        if (StringUtils.isNotBlank(checkcode) && checkcode.equals(validateCode)) {
            // User user = userService.login(model);
            // if (user != null) {
            // ServletActionContext.getRequest().getSession().setAttribute("loginUser",
            // user);
            // return HOME;
            // } else {
            // this.addActionError("输入账号密码错误");
            // return LOGIN;
            // }

            Subject subject = SecurityUtils.getSubject();
            AuthenticationToken token = new UsernamePasswordToken(model.getUsername(),
                    MD5Utils.md5(model.getPassword()));// 创建用户名密码令牌对象
            try {
                subject.login(token); // 调用内置的登录方法来实现检验
            } catch (Exception e) {
                e.printStackTrace();
                return LOGIN;
            }
            User user = (User) subject.getPrincipal();
            ServletActionContext.getRequest().getSession().setAttribute("loginUser", user);
            return HOME;

        } else {
            this.addActionError("输入验证码错误");
            return LOGIN;
        }

    }

    /*
     * 注销
     */
    public String exit() {
        ServletActionContext.getRequest().getSession().invalidate();
        return LOGIN;
    }

    /*
     * 用户修改密码
     */
    public String editPassword() throws IOException {
        String f = "1";
        User user = BOSUtils.getLoginUser(); // 获取当前用户
        try {
            userService.editPassword(user.getId(), model.getPassword());
        } catch (Exception e) {
            f = "0";
            e.printStackTrace();
        }
        ServletActionContext.getResponse().setContentType("text/html;charset=utf-8");
        ServletActionContext.getResponse().getWriter().write(f);
        return null;
    }

    private String[] roleIds;

    public void setRoleIds(String[] roleIds) {
        this.roleIds = roleIds;
    }

    /**
     * 保存用户
     * 
     * @return
     */
    public String save() {
        userService.save(model, roleIds);
        return "list";
    }

    /**
     * 分页查询
     * @return
     */
    public String pageQuery() {
        DetachedCriteria criteria = pageBean.getCriteria();
        criteria.add(Restrictions.ne("username", "admin"));
        userService.pageQuery(pageBean);
        this.objectToString(pageBean,new String[]{"noticebills","roles"});
        return null;

    }
    
    
    private String ids;
    
    public void setIds(String ids) {
        this.ids = ids;
    }
    /**
     * @return
     */
    public String delete(){
        userService.delete(ids);
        return null;
    }
   
}
