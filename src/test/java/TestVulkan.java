import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;
import org.lwjgl.vulkan.*;
import org.lwjgl.vulkan.awt.AWTVKCanvas;
import org.lwjgl.vulkan.awt.VKData;

import static org.lwjgl.system.Configuration.DEBUG;
import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.EXTDebugReport.*;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT;
import static org.lwjgl.vulkan.KHRDisplaySwapchain.*;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.EXTMetalSurface.VK_EXT_METAL_SURFACE_EXTENSION_NAME;
import static org.lwjgl.vulkan.KHRSurface.VK_KHR_SURFACE_EXTENSION_NAME;
import static org.lwjgl.vulkan.KHRWin32Surface.VK_KHR_WIN32_SURFACE_EXTENSION_NAME;
import static org.lwjgl.vulkan.KHRXlibSurface.VK_KHR_XLIB_SURFACE_EXTENSION_NAME;


/**
 * Shows how to create a simple Vulkan instance and a {@link }.
 *
 * @author Kai Burjack
 */
public class TestVulkan {
    private static long debugMessenger;
    private static final boolean ENABLE_VALIDATION_LAYERS = DEBUG.get(true);
    private static final Set<String> VALIDATION_LAYERS;
    static {
        if(ENABLE_VALIDATION_LAYERS) {
            VALIDATION_LAYERS = new HashSet<>();
            VALIDATION_LAYERS.add("VK_LAYER_KHRONOS_validation");
        } else {
            // We are not going to use it, so we don't create it
            VALIDATION_LAYERS = null;
        }
    }

    private static PointerBuffer validationLayersAsPointerBuffer() {

        MemoryStack stack = stackGet();

        PointerBuffer buffer = stack.mallocPointer(VALIDATION_LAYERS.size());

        VALIDATION_LAYERS.stream()
                .map(stack::UTF8)
                .forEach(buffer::put);

        return buffer.rewind();
    }

    private static int debugCallback(int messageSeverity, int messageType, long pCallbackData, long pUserData) {

        VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);

        System.err.println("Validation layer: " + callbackData.pMessageString());

        return VK_FALSE;
    }

    private static void populateDebugMessengerCreateInfo(VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo) {
        debugCreateInfo.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT);
        debugCreateInfo.messageSeverity(VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT);
        debugCreateInfo.messageType(VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT);
        debugCreateInfo.pfnUserCallback(TestVulkan::debugCallback);
    }
    /**
     * Create a Vulkan instance using LWJGL 3.
     *
     * @return the VkInstance handle
     */
    private static int createDebugUtilsMessengerEXT(VkInstance instance, VkDebugUtilsMessengerCreateInfoEXT createInfo,
                                                    VkAllocationCallbacks allocationCallbacks, LongBuffer pDebugMessenger) {

        if(vkGetInstanceProcAddr(instance, "vkCreateDebugUtilsMessengerEXT") != NULL) {
            return vkCreateDebugUtilsMessengerEXT(instance, createInfo, allocationCallbacks, pDebugMessenger);
        }

        return VK_ERROR_EXTENSION_NOT_PRESENT;
    }
    private static void setupDebugMessenger(VkInstance instance) {

        if(!ENABLE_VALIDATION_LAYERS) {
            return;
        }

        try(MemoryStack stack = stackPush()) {

            VkDebugUtilsMessengerCreateInfoEXT createInfo = VkDebugUtilsMessengerCreateInfoEXT.callocStack(stack);

            populateDebugMessengerCreateInfo(createInfo);

            LongBuffer pDebugMessenger = stack.longs(VK_NULL_HANDLE);

            if(createDebugUtilsMessengerEXT(instance, createInfo, null, pDebugMessenger) != VK_SUCCESS) {
                throw new RuntimeException("Failed to set up debug messenger");
            }

            debugMessenger = pDebugMessenger.get(0);
        }
    }
    private static VkInstance createInstance() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkApplicationInfo appInfo = VkApplicationInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                    .pApplicationName(stack.UTF8("AWT Vulkan Demo"))
                    .pEngineName(stack.UTF8(""))
                    .apiVersion(VK_MAKE_VERSION(1, 0, 2));

            // Enhanced switch statement would work better :(
            String surfaceExtension;
            switch (Platform.get()) {
                case WINDOWS: {
                    surfaceExtension = VK_KHR_WIN32_SURFACE_EXTENSION_NAME;
                    break;
                }
                case LINUX: {
                    surfaceExtension = VK_KHR_XLIB_SURFACE_EXTENSION_NAME;
                    break;
                }
                case MACOSX: {
                    surfaceExtension = VK_EXT_METAL_SURFACE_EXTENSION_NAME;
                    break;
                }
                default:
                    throw new RuntimeException("Failed to find the appropriate platform surface extension.");
            }


            ByteBuffer VK_KHR_SURFACE_EXTENSION = stack.UTF8(VK_KHR_SURFACE_EXTENSION_NAME);
            ByteBuffer VK_KHR_OS_SURFACE_EXTENSION = stack.UTF8(surfaceExtension);

            PointerBuffer ppEnabledExtensionNames = stack.mallocPointer(ENABLE_VALIDATION_LAYERS?3:2);
            ppEnabledExtensionNames.put(VK_KHR_SURFACE_EXTENSION);
            ppEnabledExtensionNames.put(VK_KHR_OS_SURFACE_EXTENSION);
            if (ENABLE_VALIDATION_LAYERS) ppEnabledExtensionNames.put(stack.UTF8(VK_EXT_DEBUG_UTILS_EXTENSION_NAME));
            ppEnabledExtensionNames.flip();

            VkInstanceCreateInfo pCreateInfo = VkInstanceCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                    .pNext(0L)
                    .pApplicationInfo(appInfo);
            if (ppEnabledExtensionNames.remaining() > 0) {
                pCreateInfo.ppEnabledExtensionNames(ppEnabledExtensionNames);
            }
            if (ENABLE_VALIDATION_LAYERS){
                pCreateInfo.ppEnabledLayerNames(validationLayersAsPointerBuffer());
                VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo = VkDebugUtilsMessengerCreateInfoEXT.callocStack(stack);
                populateDebugMessengerCreateInfo(debugCreateInfo);
                pCreateInfo.pNext(debugCreateInfo.address());
            }


            PointerBuffer pInstance = stack.mallocPointer(1);
            int err = vkCreateInstance(pCreateInfo, null, pInstance);
            if (err != VK_SUCCESS) {
                throw new RuntimeException("Failed to create VkInstance: " + VKUtil.translateVulkanResult(err));
            }
            long instance = pInstance.get(0);
            VkInstance vkInstance=new VkInstance(instance, pCreateInfo);
            setupDebugMessenger(vkInstance);
            return vkInstance ;

        }
    }
    private static void destroyDebugUtilsMessengerEXT(VkInstance instance, long debugMessenger, VkAllocationCallbacks allocationCallbacks) {

        if(vkGetInstanceProcAddr(instance, "vkDestroyDebugUtilsMessengerEXT") != NULL) {
            vkDestroyDebugUtilsMessengerEXT(instance, debugMessenger, allocationCallbacks);
        }

    }

    public static void main(String[] args) {
        // Create the Vulkan instance
        VkInstance instance = createInstance();
        VKData data = new VKData(); // <- set Vulkan instance
        data.instance=instance;
        JFrame frame = new JFrame("AWT test");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setPreferredSize(new Dimension(600, 600));
        AWTVKCanvas awtvkCanvas = new AWTVKCanvas(data) {
            private static final long serialVersionUID = 1L;

            public void initVK() {
                long surface = this.surface;

                // Do something with surface...
            }

            public void paintVK() {
            }
        };
        frame.add(awtvkCanvas, BorderLayout.CENTER);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                KHRSurface.vkDestroySurfaceKHR(instance, awtvkCanvas.surface, null);
                if(ENABLE_VALIDATION_LAYERS) {
                    destroyDebugUtilsMessengerEXT(instance, debugMessenger, null);
                }
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    public static class VKUtil {

        /**
         * Translates a Vulkan {@code VkResult} value to a String describing the result.
         *
         * @param result
         *            the {@code VkResult} value
         *
         * @return the result description
         */
        public static String translateVulkanResult(int result) {
            switch (result) {
                // Success codes
                case VK_SUCCESS:
                    return "Command successfully completed.";
                case VK_NOT_READY:
                    return "A fence or query has not yet completed.";
                case VK_TIMEOUT:
                    return "A wait operation has not completed in the specified time.";
                case VK_EVENT_SET:
                    return "An event is signaled.";
                case VK_EVENT_RESET:
                    return "An event is unsignaled.";
                case VK_INCOMPLETE:
                    return "A return array was too small for the result.";
                case VK_SUBOPTIMAL_KHR:
                    return "A swapchain no longer matches the surface properties exactly, but can still be used to present to the surface successfully.";

                // Error codes
                case VK_ERROR_OUT_OF_HOST_MEMORY:
                    return "A host memory allocation has failed.";
                case VK_ERROR_OUT_OF_DEVICE_MEMORY:
                    return "A device memory allocation has failed.";
                case VK_ERROR_INITIALIZATION_FAILED:
                    return "Initialization of an object could not be completed for implementation-specific reasons.";
                case VK_ERROR_DEVICE_LOST:
                    return "The logical or physical device has been lost.";
                case VK_ERROR_MEMORY_MAP_FAILED:
                    return "Mapping of a memory object has failed.";
                case VK_ERROR_LAYER_NOT_PRESENT:
                    return "A requested layer is not present or could not be loaded.";
                case VK_ERROR_EXTENSION_NOT_PRESENT:
                    return "A requested extension is not supported.";
                case VK_ERROR_FEATURE_NOT_PRESENT:
                    return "A requested feature is not supported.";
                case VK_ERROR_INCOMPATIBLE_DRIVER:
                    return "The requested version of Vulkan is not supported by the driver or is otherwise incompatible for implementation-specific reasons.";
                case VK_ERROR_TOO_MANY_OBJECTS:
                    return "Too many objects of the type have already been created.";
                case VK_ERROR_FORMAT_NOT_SUPPORTED:
                    return "A requested format is not supported on this device.";
                case VK_ERROR_SURFACE_LOST_KHR:
                    return "A surface is no longer available.";
                case VK_ERROR_NATIVE_WINDOW_IN_USE_KHR:
                    return "The requested window is already connected to a VkSurfaceKHR, or to some other non-Vulkan API.";
                case VK_ERROR_OUT_OF_DATE_KHR:
                    return "A surface has changed in such a way that it is no longer compatible with the swapchain, and further presentation requests using the "
                            + "swapchain will fail. Applications must query the new surface properties and recreate their swapchain if they wish to continue" + "presenting to the surface.";
                case VK_ERROR_INCOMPATIBLE_DISPLAY_KHR:
                    return "The display used by a swapchain does not use the same presentable image layout, or is incompatible in a way that prevents sharing an" + " image.";
                case VK_ERROR_VALIDATION_FAILED_EXT:
                    return "A validation layer found an error.";
                default:
                    return String.format("%s [%d]", "Unknown", result);
            }
        }

    }
}



