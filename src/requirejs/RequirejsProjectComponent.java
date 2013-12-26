package requirejs;

import com.intellij.lang.javascript.JSElementTypes;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.impl.JSFileImpl;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.TreeElement;
import org.jetbrains.annotations.NotNull;
import requirejs.settings.Settings;

import java.util.HashMap;

public class RequirejsProjectComponent implements ProjectComponent
{
    protected Project project;
    protected Settings settings;
    protected boolean settingValidStatus;
    protected String settingValidVersion;

    final protected static Logger LOG = Logger.getInstance("Requirejs-Plugin");
    private VirtualFile requirejsBasePath;

    public RequirejsProjectComponent(Project project) {
        this.project = project;
        settings = Settings.getInstance(project);
    }

    @Override
    public void projectOpened() {
        validateSettings();
    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "RequirejsProjectComponent";
    }

    public static Logger getLogger() {
        return LOG;
    }

    public boolean isEnabled() {
        return Settings.getInstance(project).pluginEnabled;
    }

    public boolean isSettingsValid(){
        return settingValidStatus;
    }

    public boolean validateSettings()
    {
        if (null == getWebDir()) {
            showErrorConfigNotification("Web path not found");
            settingValidStatus = false;
            return false;
        }

        settingValidStatus = true;
        return true;
    }

    protected void showErrorConfigNotification(String content) {
        if (!settings.getVersion().equals(settingValidVersion)) {
            showInfoNotification(content, NotificationType.ERROR);
        }
    }

    public VirtualFile getWebDir() {
        return project.getBaseDir().findFileByRelativePath(settings.webPath);
    }

    public void showInfoNotification(String content) {
        this.showInfoNotification(content, NotificationType.WARNING);
    }

    public void showInfoNotification(String content, NotificationType type) {
        Notification errorNotification = new Notification("Require.js plugin", "Require.js plugin", content, type);
        Notifications.Bus.notify(errorNotification, this.project);
    }

    protected void parseRequirejsConfig()
    {
        VirtualFile mainJsVirtualFile = getWebDir()
                .findFileByRelativePath(
                        settings.mainJsPath
                );
        if (null == mainJsVirtualFile) {
            this.showInfoNotification("Config file not found");
        } else {
            PsiFile mainJs = PsiManager
                    .getInstance(project)
                    .findFile(
                            mainJsVirtualFile
                    );
            if (mainJs instanceof JSFileImpl) {
                if (((JSFileImpl) mainJs).getTreeElement() == null) {
//                    requirejsConfigAliasesMap = parseMainJsFile(((JSFileImpl) mainJs).calcTreeElement());
                } else {
//                    requirejsConfigAliasesMap = parseMainJsFile(((JSFileImpl) mainJs).getTreeElement());
                }
            } else {
                this.showInfoNotification("Config file wrong format");
            }
        }
    }

    public HashMap<String, VirtualFile> parseMainJsFile(TreeElement node) {
        HashMap<String, VirtualFile> list = new HashMap<String, VirtualFile>();

        TreeElement firstChild = node.getFirstChildNode();
        if (firstChild != null) {
            list.putAll(parseMainJsFile(firstChild));
        }

        TreeElement nextNode = node.getTreeNext();
        if (nextNode != null) {
            list.putAll(parseMainJsFile(nextNode));
        }

        if (node.getElementType() == JSTokenTypes.IDENTIFIER) {
            try {
                String requirejsFunctionName = Settings
                        .getInstance(project)
                        .requireFunctionName;
                if (node.getText().equals(requirejsFunctionName)) {
                    list.putAll(
                            parseRequirejsConfig(
                                    (TreeElement) node
                                            .getTreeParent()
                                            .getTreeNext()
                                            .findChildByType(JSElementTypes.OBJECT_LITERAL_EXPRESSION)
                                            .getFirstChildNode()
                            )
                    );
                }
            } catch (NullPointerException ignored) {}
        }

        return list;
    }

    public HashMap<String, VirtualFile> parseRequirejsConfig(TreeElement node) {
        HashMap<String, VirtualFile> list = new HashMap<String, VirtualFile>();
        if (null == node) {
            return list;
        }

        TreeElement next = node.getTreeNext();
        if (null != next) {
            list.putAll(parseRequirejsConfig(next));
        }

        try {
            if (node.getElementType() == JSElementTypes.PROPERTY) {
                TreeElement identifier = (TreeElement) node.findChildByType(JSTokenTypes.IDENTIFIER);
                if (null != identifier) {
                    String identifierName = identifier.getText();
                    if (identifierName.equals("baseUrl")) {
                        String baseUrl;

                        baseUrl = node
                                .findChildByType(JSElementTypes.LITERAL_EXPRESSION)
                                .getText().replace("\"", "").replace("'","");
                        baseUrl = settings
                                .webPath
                                .concat(baseUrl);
                        requirejsBasePath = project
                                .getBaseDir()
                                .findFileByRelativePath(baseUrl);
                    }
                    if (identifierName.equals("paths")) {
                        list.putAll(
                                parseRequireJsPaths(
                                        (TreeElement) node
                                                .findChildByType(JSElementTypes.OBJECT_LITERAL_EXPRESSION)
                                                .getFirstChildNode()
                                )
                        );
                    }
                }
            }
        } catch (NullPointerException ignored) {}

        return list;
    }

    protected HashMap<String, VirtualFile> parseRequireJsPaths(TreeElement node) {
        HashMap<String, VirtualFile> list = new HashMap<String, VirtualFile>();
        if (null == node) {
            return list;
        }

        TreeElement next = node.getTreeNext();
        if (null != next) {
            list.putAll(parseRequireJsPaths(next));
        }

        if (node.getElementType() == JSElementTypes.PROPERTY) {
            TreeElement path = (TreeElement) node.findChildByType(JSElementTypes.LITERAL_EXPRESSION);
            TreeElement alias = (TreeElement) node.getFirstChildNode();
            if (null != path && null != alias) {
                String pathString = path.getText().replace("\"","").replace("'", "").concat(".js");
                String aliasString = alias.getText().replace("\"","").replace("'", "").concat(".js");

                VirtualFile pathVF = getWebDir().findFileByRelativePath(pathString);
                if (null != pathVF) {
                    list.put(aliasString, pathVF);
                } else {
                    pathVF = requirejsBasePath.findFileByRelativePath(pathString);
                    if (null != pathVF) {
                        list.put(aliasString, pathVF);
                    }
                }
            }
        }

        return list;
    }
}
